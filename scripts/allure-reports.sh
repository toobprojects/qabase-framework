#!/usr/bin/env bash
#
# QABase – Tests + Allure Reports (Aggregator)
# ---------------------------------------------------------------
# Purpose
#   Runs the full reactor tests with the `allure-reports` profile enabled,
#   then generates **one** aggregated Allure HTML report at the repo root
#   using a non-recursive Maven invocation. Optionally opens or serves it.
#
# TL;DR
#   ./allure-reports.sh                # run tests + build report + auto-open
#   ./allure-reports.sh --no-open      # run tests + build report (don’t open)
#   ./allure-reports.sh --serve        # run tests + build report + serve via Allure CLI
#   ./allure-reports.sh --help         # usage
#   ./allure-reports.sh --mvn-args "-Dtest=*IT"  # pass extra Maven args
#
set -Eeuo pipefail

# --- Pretty logging ---------------------------------------------------------
blue="\033[1;34m"; green="\033[1;32m"; red="\033[1;31m"; yellow="\033[1;33m"; dim="\033[2m"; reset="\033[0m"
log()  { printf "%b[QABase]%b %s\n" "${blue}" "${reset}" "$*"; }
ok()   { printf "%b[✔]%b %s\n" "${green}" "${reset}" "$*"; }
warn() { printf "%b[!]%b %s\n" "${yellow}" "${reset}" "$*"; }
err()  { printf "%b[x]%b %s\n" "${red}" "${reset}" "$*"; } 

# --- Defaults & args --------------------------------------------------------
OPEN_AFTER=true
SERVE=false
MVN_ARGS=""

show_help() {
  cat <<'USAGE'
🧪 QABase – Generate Allure report (tests + HTML)

Usage:
  ./allure-reports.sh [options]

Options:
  -h, --help          Show this help and exit
  --no-open           Do not open the generated HTML report automatically
  --serve             After generating, run `allure serve` on the collected results
  --mvn-args "..."    Extra args forwarded to both Maven invocations

What it does:
  1) mvn clean verify -Pallure-reports         🧪 runs tests & writes allure-results per module
  2) mvn -N -Pallure-reports allure:report     📊 aggregates to target/allure-reports (root only)

Notes:
  • Allure CLI is only required when using --serve.
  • The parent POM's `allure-reports` profile should:
      - add io.qameta.allure:allure-junit5 (test scope)
      - copy **/target/allure-results/** into ${project.build.directory}/allure-results (root)
      - run io.qameta.allure:allure-maven to build HTML
USAGE
}

# Parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) show_help; exit 0;;
    --no-open) OPEN_AFTER=false; shift;;
    --serve)   SERVE=true; shift;;
    --mvn-args) MVN_ARGS="$2"; shift 2;;
    *) err "Unknown option: $1"; echo; show_help; exit 1;;
  esac
done

# --- Preconditions ----------------------------------------------------------
command -v mvn >/dev/null 2>&1 || { err "Maven not found on PATH"; exit 127; }
if $SERVE; then
  command -v allure >/dev/null 2>&1 || { err "Allure CLI not found on PATH (required for --serve)"; exit 127; }
fi

# Move to repo root (directory of this script)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
REPO_ROOT="$PWD"

RESULTS_ROOT_DIR="${REPO_ROOT}/target/allure-results"
REPORT_INDEX="${REPO_ROOT}/target/allure-reports/index.html"

log "🧪 Running tests with profile: allure-reports"
log "    → mvn clean verify -Pallure-reports ${MVN_ARGS}"
mvn clean verify -Pallure-reports ${MVN_ARGS}

# Quick sanity: do we have any results in the reactor?
if [[ ! -d "$RESULTS_ROOT_DIR" ]] && ! ls -1 **/target/allure-results/*.json >/dev/null 2>&1; then
  warn "No allure-results detected yet. The root aggregation step will try to collect them."
fi

log "📊 Aggregating & generating HTML report at root (non-recursive)"
log "    → mvn -N -Pallure-reports -DskipTests allure:report ${MVN_ARGS}"
mvn -N -Pallure-reports -DskipTests allure:report ${MVN_ARGS}

if [[ -f "$REPORT_INDEX" ]]; then
  ok "Allure report ready: ${REPORT_INDEX}"
else
  warn "Report index not found at ${REPORT_INDEX}. Check the Maven logs above."
fi

# Open or serve, if requested
if [[ -f "$REPORT_INDEX" && "$OPEN_AFTER" == true ]]; then
  log "🌐 Opening report in your default browser..."
  case "$(uname -s)" in
    Darwin) open "$REPORT_INDEX" || true;;
    Linux)  xdg-open "$REPORT_INDEX" >/dev/null 2>&1 || true;;
    MINGW*|MSYS*|CYGWIN*) start "" "$REPORT_INDEX" || true;;
    *) warn "Cannot auto-open on this OS. Open manually: $REPORT_INDEX";;
  esac
fi

if $SERVE; then
  log "🚀 Starting temporary Allure server over detected results (Ctrl+C to stop)"
  # Collect unique allure-results directories that contain JSON
  mapfile -t FOUND_DIRS < <(find "$REPO_ROOT" -type d -path "*/target/allure-results" -exec bash -lc 'shopt -s nullglob; for f in "$1"/*.json; do echo "$1"; break; done' _ {} \; | sort -u)
  if (( ${#FOUND_DIRS[@]} == 0 )); then
    err "No allure-results found to serve."; exit 2
  fi
  allure serve "${FOUND_DIRS[@]}"
fi

ok "Done."
#!/usr/bin/env bash
#
# QABase – Tests + Allure Reports (Aggregator)
# ---------------------------------------------------------------
# Purpose
#   Runs the full reactor tests with the `allure-reports` profile enabled,
#   then generates **one** aggregated Allure HTML report at the repo root
#   using a non-recursive Maven invocation. Optionally opens or serves it.
#
# TL;DR
#   ./allure-reports.sh                # run tests + build report + auto-open
#   ./allure-reports.sh --no-open      # run tests + build report (don’t open)
#   ./allure-reports.sh --serve        # run tests + build report + serve via Allure CLI
#   ./allure-reports.sh --help         # usage
#   ./allure-reports.sh --mvn-args "-Dtest=*IT"  # pass extra Maven args
#
set -Eeuo pipefail

# --- Pretty logging ---------------------------------------------------------
blue="\033[1;34m"; green="\033[1;32m"; red="\033[1;31m"; yellow="\033[1;33m"; dim="\033[2m"; reset="\033[0m"
log()  { printf "%b[QABase]%b %s\n" "${blue}" "${reset}" "$*"; }
ok()   { printf "%b[✔]%b %s\n" "${green}" "${reset}" "$*"; }
warn() { printf "%b[!]%b %s\n" "${yellow}" "${reset}" "$*"; }
err()  { printf "%b[x]%b %s\n" "${red}" "${reset}" "$*"; } 

# --- Defaults & args --------------------------------------------------------
OPEN_AFTER=true
SERVE=false
MVN_ARGS=""

show_help() {
  cat <<'USAGE'
🧪 QABase – Generate Allure report (tests + HTML)

Usage:
  ./allure-reports.sh [options]

Options:
  -h, --help          Show this help and exit
  --no-open           Do not open the generated HTML report automatically
  --serve             After generating, run `allure serve` on the collected results
  --mvn-args "..."    Extra args forwarded to both Maven invocations

What it does:
  1) mvn clean verify -Pallure-reports         🧪 runs tests & writes allure-results per module
  2) mvn -N -Pallure-reports allure:report     📊 aggregates to target/allure-reports (root only)

Notes:
  • Allure CLI is only required when using --serve.
  • The parent POM's `allure-reports` profile should:
      - add io.qameta.allure:allure-junit5 (test scope)
      - copy **/target/allure-results/** into ${project.build.directory}/allure-results (root)
      - run io.qameta.allure:allure-maven to build HTML
USAGE
}

# Parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) show_help; exit 0;;
    --no-open) OPEN_AFTER=false; shift;;
    --serve)   SERVE=true; shift;;
    --mvn-args) MVN_ARGS="$2"; shift 2;;
    *) err "Unknown option: $1"; echo; show_help; exit 1;;
  esac
done

# --- Preconditions ----------------------------------------------------------
command -v mvn >/dev/null 2>&1 || { err "Maven not found on PATH"; exit 127; }
if $SERVE; then
  command -v allure >/dev/null 2>&1 || { err "Allure CLI not found on PATH (required for --serve)"; exit 127; }
fi

# Move to repo root (directory of this script)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
REPO_ROOT="$PWD"

RESULTS_ROOT_DIR="${REPO_ROOT}/target/allure-results"
REPORT_INDEX="${REPO_ROOT}/target/allure-reports/index.html"

log "🧪 Running tests with profile: allure-reports"
log "    → mvn clean verify -Pallure-reports ${MVN_ARGS}"
mvn clean verify -Pallure-reports ${MVN_ARGS}

# Quick sanity: do we have any results in the reactor?
if [[ ! -d "$RESULTS_ROOT_DIR" ]] && ! ls -1 **/target/allure-results/*.json >/dev/null 2>&1; then
  warn "No allure-results detected yet. The root aggregation step will try to collect them."
fi

log "📊 Aggregating & generating HTML report at root (non-recursive)"
log "    → mvn -N -Pallure-reports -DskipTests allure:report ${MVN_ARGS}"
mvn -N -Pallure-reports -DskipTests allure:report ${MVN_ARGS}

if [[ -f "$REPORT_INDEX" ]]; then
  ok "Allure report ready: ${REPORT_INDEX}"
else
  warn "Report index not found at ${REPORT_INDEX}. Check the Maven logs above."
fi

# Open or serve, if requested
if [[ -f "$REPORT_INDEX" && "$OPEN_AFTER" == true ]]; then
  log "🌐 Opening report in your default browser..."
  case "$(uname -s)" in
    Darwin) open "$REPORT_INDEX" || true;;
    Linux)  xdg-open "$REPORT_INDEX" >/dev/null 2>&1 || true;;
    MINGW*|MSYS*|CYGWIN*) start "" "$REPORT_INDEX" || true;;
    *) warn "Cannot auto-open on this OS. Open manually: $REPORT_INDEX";;
  esac
fi

if $SERVE; then
  log "🚀 Starting temporary Allure server over detected results (Ctrl+C to stop)"
  # Collect unique allure-results directories that contain JSON
  mapfile -t FOUND_DIRS < <(find "$REPO_ROOT" -type d -path "*/target/allure-results" -exec bash -lc 'shopt -s nullglob; for f in "$1"/*.json; do echo "$1"; break; done' _ {} \; | sort -u)
  if (( ${#FOUND_DIRS[@]} == 0 )); then
    err "No allure-results found to serve."; exit 2
  fi
  allure serve "${FOUND_DIRS[@]}"
fi

ok "Done."