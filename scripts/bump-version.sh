#!/usr/bin/env bash


# ================================================================================================================
#
# @author: Thabo Lebogang Matjuda
# @since: 15 Aug 2024
#
# ================================================================================================================


# -e : Exit immediately if a command exits with a non-zero status.
# -u : Treat unset variables as an error when substituting.
# -o pipefail : The return value of a pipeline is the status of the last command to exit with a non-zero status, or zero if all commands of the pipeline exit successfully.
set -euo pipefail

# Bumps only the project/module versions using Maven Versions Plugin.
# It will NOT change dependency versions.
#
# Usage:
#   ./bump-version.sh 1.2.3 [--snapshot] [--allow-dirty] [--tag] [--push]
#
# Examples:
#   ./bump-version.sh 0.1.0
#   ./bump-version.sh 0.2.0 --snapshot
#   ./bump-version.sh 1.0.0 --tag --push


# The selected code defines a usage function that prints instructions on how to use the script and then exits with status
#   1. It explains the required argument (<newVersion>) and available options, and warns not to include -SNAPSHOT directly in the version.
function usage() {
  echo "📖 Usage: $0 <newVersion> [--snapshot] [--allow-dirty] [--tag] [--push]"
  echo "🔢 newVersion: SemVer MAJOR.MINOR.PATCH, e.g., 1.2.3 or 1.2.3-rc.1"
  echo "⚠️  Do not include -SNAPSHOT in <newVersion>; use --snapshot."
  exit 1
}

# The selected code defines a function that checks if a given version string matches the Semantic Versioning (SemVer) format.
# It uses a regular expression to validate the version string against the SemVer specification, which includes
#   major, minor, and patch numbers, as well as optional pre-release and build metadata
function is_valid_version() {
  [[ "$1" =~ ^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z.-]+)?(\+[0-9A-Za-z.-]+)?$ ]]
}

# Check if at least one argument is provided; if not, show usage.
# The script expects the first argument to be a version string.
# If the first argument is -h or --help, it shows usage instructions.
# $# : The number of arguments passed.
if [[ $# -lt 1 ]]; then usage; fi

# Checking if the frst argument is -h or --help to show usage instructions.
# If it is, the script calls the usage function to display how to use the script.
case "${1:-}" in
  -h|--help) usage ;;
esac

# The first argument is the new version to set.
# It is expected to be in Semantic Versioning (SemVer) format.
# The script will validate this version and use it to update the project version.
NEW_VERSION="$1"

# Refuse options as version (e.g., '--help')
# If the first argument starts with a dash, it is treated as an option, not a version.
# The script checks if the first argument starts with a dash (-) and if so,
# ... it prints an error message and calls the usage function.
# This prevents the script from interpreting options as version numbers.
# If the first argument is a version that starts with a dash, it will be treated
if [[ "$NEW_VERSION" == -* ]]; then
  echo "❌ First argument must be a version, not an option."
  usage
fi

# Normalize: strip leading 'v' (e.g., v1.2.3 -> 1.2.3)
if [[ "$NEW_VERSION" =~ ^v[0-9] ]]; then
  echo "ℹ️  Stripping leading 'v' from version '${NEW_VERSION}'"
  NEW_VERSION="${NEW_VERSION#v}"
fi

# Disallow passing -SNAPSHOT directly; use the flag
if [[ "$NEW_VERSION" =~ -SNAPSHOT$ ]]; then
  echo "❌ Do not include -SNAPSHOT in <newVersion>. Pass the base version and use --snapshot."
  exit 1
fi

# Strict SemVer validation
if ! is_valid_version "$NEW_VERSION"; then
  echo "❌ Invalid version: '$NEW_VERSION'"
  echo "   Expected SemVer: MAJOR.MINOR.PATCH, e.g., 1.2.3 or 1.2.3-rc.1"
  exit 1
fi

# Remove the first argument (the version) so only options remain for parsing.
# "shift" drops $1 and shifts the rest left (so $2 becomes $1, etc.).
# If no arguments are left, "shift" returns an error — and since we are using "set -e",
# that would normally exit the script. Appending "|| true" prevents the script from failing
# in that case, acting as a safe guard.
shift || true

APPEND_SNAPSHOT=false
ALLOW_DIRTY=false
CREATE_TAG=false
PUSH_TAG=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --snapshot) APPEND_SNAPSHOT=true ;;
    --allow-dirty) ALLOW_DIRTY=true ;;
    --tag) CREATE_TAG=true ;;
    --push) PUSH_TAG=true ;;
    -h|--help) usage ;;
    *) echo "Unknown option: $1"; usage ;;
  esac
  shift
done

# Check if we are in a directory with a pom.xml file.
if [[ ! -f "pom.xml" ]]; then
  echo "❌ No pom.xml in current directory. Run this from your project root."
  exit 1
fi

# Check if maven is installed.
if ! command -v mvn >/dev/null 2>&1; then
  echo "❌ Maven (mvn) not found on PATH."
  exit 1
fi

# Allow to bump version even if the working tree is dirty.
# If the --allow-dirty flag is not set, the script checks if the working tree is clean.
# If it is dirty (i.e., there are uncommitted changes), it prints an error message and exits with status 1.
# If the flag is set, it skips
if ! $ALLOW_DIRTY; then
  if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    if [[ -n "$(git status --porcelain)" ]]; then
      echo "❌ Working tree is dirty. Commit or stash changes, or use --allow-dirty."
      exit 1
    fi
  fi
fi

# Compose final version
FINAL_VERSION="$NEW_VERSION"
$APPEND_SNAPSHOT && FINAL_VERSION="${FINAL_VERSION}-SNAPSHOT"

echo "🔧 Bumping project version to: ${FINAL_VERSION}"
echo "   (This only updates <version> for the parent and modules; dependencies remain unchanged)"

# Use Maven Versions Plugin to update all modules' project version.
mvn -q versions:set \
  -DnewVersion="${FINAL_VERSION}" \
  -DprocessAllModules=true \
  -DgenerateBackupPoms=false

# Optional: sanity check – print the effective version at the root
ROOT_VER="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version || true)"
echo "✅ Root project now reports version: ${ROOT_VER}"

# If in a Git repo, create a tag/commit if asked
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  # Stage and commit the changed POMs
  if [[ -n "$(git status --porcelain | grep pom.xml || true)" ]]; then
    git add **/pom.xml pom.xml 2>/dev/null || git add pom.xml
    git commit -m "chore: bump project version to ${FINAL_VERSION}"
    echo "📝 Committed version bump."
  fi

  if $CREATE_TAG; then
    TAG="v${FINAL_VERSION}"
    git tag -a "${TAG}" -m "Release ${TAG}"
    echo "🏷️  Created tag ${TAG}"
    if $PUSH_TAG; then
      git push && git push --tags
      echo "⬆️  Pushed commit and tags."
    fi
  fi
fi

echo "🎉 Done."