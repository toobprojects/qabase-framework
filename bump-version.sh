#!/usr/bin/env bash
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

usage() {
  echo "Usage: $0 <newVersion> [--snapshot] [--allow-dirty] [--tag] [--push]"
  echo "  newVersion: SemVer MAJOR.MINOR.PATCH, e.g., 1.2.3 or 1.2.3-rc.1"
  echo "  Do not include -SNAPSHOT in <newVersion>; use --snapshot."
  exit 1
}

is_valid_version() {
  [[ "$1" =~ ^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z.-]+)?(\+[0-9A-Za-z.-]+)?$ ]]
}

if [[ $# -lt 1 ]]; then usage; fi

case "${1:-}" in
  -h|--help) usage ;;
esac

NEW_VERSION="$1"

# Refuse options as version (e.g., '--help')
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

if [[ ! -f "pom.xml" ]]; then
  echo "❌ No pom.xml in current directory. Run this from your project root."
  exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "❌ Maven (mvn) not found on PATH."
  exit 1
fi

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