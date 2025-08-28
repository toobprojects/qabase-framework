# QABase – Local Test Runner (`test.sh`)

A fast, predictable way to run **local tests from the repo root** before you push, open a PR, or merge to `master`. The script works with either **Maven Wrapper** (`./mvnw`) or your system **Maven** (`mvn`), and provides helpful flags for module selection, profiles, integration tests, Allure viewing, and JDK override.

---

## Why use this script?
- Always runs from the **repository root** (avoids "no POM in this directory" errors).
- Works with `./mvnw` if present, otherwise falls back to `mvn`.
- Sensible defaults and **clear logging**.
- Short flags for common workflows: **unit tests**, **integration tests**, **module-only**, **profiles**, **Allure**.

---

## Quick Start

```bash
# run all unit tests across the repo (Surefire)
./test.sh

# run integration tests (Failsafe)
./test.sh --it

# open Allure after the run (requires Allure CLI installed)
./test.sh --allure
```

> Tip: combine flags, e.g. `./test.sh --module qabase-rest --profile ci --it --allure`

---

## Options

| Option | Description |
|---|---|
| `--module <name>` | Run tests only for a specific Maven module (e.g., `qabase-rest`). Uses `-pl <module> -am`. |
| `--profile <name>` | Activate a Maven profile (e.g., `ci`, `headless`). Equivalent to `-P <profile>`. |
| `--it` | Run **integration tests** using **Failsafe**: `clean verify -DskipITs=false -DskipTests`. |
| `--allure` | After tests, try to open the **Allure** report via the CLI (`allure serve`). |
| `--jdk <path>` | Temporarily use a specific JDK: sets `JAVA_HOME` and prepends `PATH`. |
| `-h`, `--help` | Show usage help and exit. |

The script logs the resolved repo root, current Java and Maven versions, and the exact Maven command it executes.

---

## Common Workflows

### 1) Full local sanity check (before pushing)
```bash
./test.sh
```
Runs: `mvn clean test -DskipTests=false` across all modules.

### 2) Verify integration scenarios
```bash
./test.sh --it
```
Runs: `mvn clean verify -DskipITs=false -DskipTests` (Failsafe). Use this when your project separates ITs from unit tests.

### 3) Focus on a single module
```bash
./test.sh --module qabase-rest
```
Runs that module (and any required dependencies) only.

### 4) Use a profile (e.g., CI/headless)
```bash
./test.sh --profile ci
```
Activates `-P ci` for the whole run.

### 5) Open Allure after tests
```bash
./test.sh --allure
```
Searches for the first `allure-results` directory (depth ≤ 3) and runs `allure serve`.

### 6) Combine everything
```bash
./test.sh --module qabase-rest --profile headless --it --allure
```
Great for debugging one area with a specific profile and viewing results immediately.

### 7) Temporarily use a specific JDK
```bash
./test.sh --jdk /Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
```
Useful when you have multiple JDKs installed.

---

## What the script actually runs

The script builds a Maven command with:
- Colorized output: `--color=always -Dstyle.color=always`
- Quiet mode for noise reduction: `-q` (you still see failures)
- Profiles via `-P <profile>` when provided
- Module targeting via `-pl <module> -am` when provided
- Goals:
  - **Unit tests**: `clean test -DskipTests=false`
  - **Integration tests**: `clean verify -DskipITs=false -DskipTests`

It prints the exact command before executing so you can copy/paste it if needed.

---

## Allure notes
- `--allure` requires the **Allure CLI** to be on your PATH.
- The script looks for the **first** `allure-results` folder (up to 3 levels deep). If none is found, you’ll see a helpful error.
- For **aggregated** reports across modules, prefer the dedicated helper: `scripts/allure-reports-serve.sh` (see the Allure guide).

---

## Exit codes
- **0**: success (all tests passed)
- **non‑zero**: a failure occurred (test failures, build errors, missing tools, etc.)

Use the exit code in pre‑commit hooks or local CI scripts if desired.

---

## Troubleshooting

**“Permission denied” running `mvnw`**
```bash
chmod +x mvnw
```

**Maven/Java not found**
- Ensure `mvn` or `./mvnw` is available.
- Check `java -version` prints correctly.
- If needed, use `--jdk <path>` to point to a specific JDK.

**Allure CLI not found**
- Install per https://docs.qameta.io/allure/ and ensure `allure` is on your PATH.

**No `allure-results` found**
- Make sure your tests actually generate Allure results.
- Some modules/profiles may disable Allure; try without `--profile` or run the aggregated report script.

---

## FAQ

**Q: Does this modify versions or perform releases?**  
A: No. This script only runs tests. For versions and releases, see the **Developer Tools** section in the README.

**Q: Can I pass raw Maven flags?**  
A: Not directly. Add commonly needed switches to the script or run the printed Maven command manually.

**Q: Where should I run it from?**  
A: Anywhere inside the repo — the script resolves and moves to the **repo root** before running Maven.

---

## Command reference (from `./test.sh --help`)

```text
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
```

---

**Happy testing!** If you need enhancements (extra flags, different defaults), propose an update and keep the guide in sync.
