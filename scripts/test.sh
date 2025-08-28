#!/usr/bin/env bash

# ================================================================================================================
# QA Base – Local test runner
# Author: Thabo Lebogang Matjuda
# Updated: 21 Aug 2025
# ------------------------------------------------------------------------------------------------
# Goals
#  - Always run Maven from the repo root (avoids "no POM in this directory" errors)
#  - Work with either mvnw or system mvn
#  - Helpful flags for module/profile/IT-only/Allure/JDK override
#  - Safe defaults & clear logging
# ================================================================================================================

set -euo pipefail

# --- utils ---------------------------------------------------------------------------------------
log()  { printf "\033[1;36m[TEST]\033[0m %s\n" "$*"; }
err()  { printf "\033[1;31m[ERR ]\033[0m %s\n" "$*" 1>&2; }
usage(){
  cat <<'USAGE'
Usage: ./test.sh [options]

Options:
  --module <name>    Run only this Maven module (e.g., qabase-rest)
  --profile <name>   Activate a Maven profile (e.g., ci, headless)
  --it               Run integration tests (failsafe) instead of unit tests
  --allure           Open Allure after the run (requires `allure` CLI)
  --jdk <path>       Use a specific JDK for this run (sets JAVA_HOME)
  -h, --help         Show this help

Examples:
  ./test.sh                      # clean test across all modules
  ./test.sh --module qabase-rest # test only REST module
  ./test.sh --profile ci         # activate CI profile
  ./test.sh --it                 # run integration tests (failsafe)
  ./test.sh --allure             # open Allure after
USAGE
}

# --- find repo root & mvn ------------------------------------------------------------------------
# Resolve script dir, then repo root (git if available, else parent of script)
SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
if git -C "$SCRIPT_DIR" rev-parse --show-toplevel &>/dev/null; then
  REPO_ROOT="$(git -C "$SCRIPT_DIR" rev-parse --show-toplevel)"
else
  # Assume the script is placed in repo root (fallback)
  REPO_ROOT="$SCRIPT_DIR"
fi
cd "$REPO_ROOT"

# Prefer Maven Wrapper if present
if [[ -x "$REPO_ROOT/mvnw" ]]; then
  MVN="$REPO_ROOT/mvnw"
else
  MVN="mvn"
fi

# --- parse args ----------------------------------------------------------------------------------
MODULE=""
PROFILE=""
RUN_IT=false
OPEN_ALLURE=false
JDK_HOME=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --module)
      MODULE="$2"; shift 2;;
    --profile)
      PROFILE="$2"; shift 2;;
    --it)
      RUN_IT=true; shift;;
    --allure)
      OPEN_ALLURE=true; shift;;
    --jdk)
      JDK_HOME="$2"; shift 2;;
    -h|--help)
      usage; exit 0;;
    *)
      err "Unknown option: $1"; usage; exit 1;;
  esac
done

# --- JDK override --------------------------------------------------------------------------------
if [[ -n "$JDK_HOME" ]]; then
  export JAVA_HOME="$JDK_HOME"
  export PATH="$JAVA_HOME/bin:$PATH"
  log "Using JAVA_HOME=$JAVA_HOME"
fi

# --- info ----------------------------------------------------------------------------------------
log "Repo root: $REPO_ROOT"
log "Java: $(java -version 2>&1 | head -n1)"
log "Maven: $($MVN -v | head -n1)"

# --- build command -------------------------------------------------------------------------------
MVN_ARGS=("-q" "--color=always" "-Dstyle.color=always")

if [[ -n "$PROFILE" ]]; then
  MVN_ARGS+=("-P" "$PROFILE")
fi

if [[ -n "$MODULE" ]]; then
  # run just the selected module
  MVN_GOAL=("-pl" "$MODULE" "-am")
else
  MVN_GOAL=( )
fi

if [[ "$RUN_IT" == true ]]; then
  # Failsafe (integration tests)
  GOALS=("clean" "verify" "-DskipITs=false" "-DskipTests")
else
  # Surefire (unit tests)
  GOALS=("clean" "test" "-DskipTests=false")
fi

log "Running: $MVN ${MVN_ARGS[*]} ${MVN_GOAL[*]} ${GOALS[*]}"
$MVN "${MVN_ARGS[@]}" ${MVN_GOAL[@]} ${GOALS[@]}

# --- Allure --------------------------------------------------------------------------------------
if [[ "$OPEN_ALLURE" == true ]]; then
  if command -v allure >/dev/null 2>&1; then
    RESULTS_DIR="$(find . -type d -name allure-results -maxdepth 3 | head -n1 || true)"
    if [[ -n "$RESULTS_DIR" ]]; then
      log "Opening Allure report for: $RESULTS_DIR"
      allure serve "$RESULTS_DIR"
    else
      err "No allure-results directory found. Did tests generate results?"
    fi
  else
    err "Allure CLI not found. Install from https://docs.qameta.io/allure/"
  fi
fi

log "Done."