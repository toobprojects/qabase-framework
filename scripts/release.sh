#!/usr/bin/env bash

# ================================================================================================================
# QA Base – Release helper
# Author: Thabo Lebogang Matjuda
# Updated: 21 Aug 2025

# release.sh - Simple helper to publish a Maven release
#
# Usage:
#   ./release.sh
#
# What it does:
#   1. Prints clear, colored log messages.
#   2. Runs the Maven deploy with the release profile.
#   3. Exits with error if Maven fails.
#

# ================================================================================================================

# --- Colors for nicer logging ---
RED="\033[0;31m"
GREEN="\033[0;32m"
YELLOW="\033[1;33m"
BLUE="\033[0;34m"
NC="\033[0m" # reset/no color

log_info()    { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }

# --- Script start ---
log_info "Starting Maven release build..."

# Use Maven Wrapper if available, fallback to mvn
MVN_CMD="./mvnw"
if [ ! -f "$MVN_CMD" ]; then
  MVN_CMD="mvn"
fi

# Run release deploy
$MVN_CMD -U -ntp clean deploy -P release
STATUS=$?

if [ $STATUS -ne 0 ]; then
  log_error "Release build failed with status code $STATUS"
  exit $STATUS
else
  log_success "Release build completed successfully!"
fi