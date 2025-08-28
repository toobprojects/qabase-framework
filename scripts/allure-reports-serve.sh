# QABase – Aggregated Allure Reports (`scripts/allure-reports-serve.sh`)

Generate and **serve a combined Allure report** for all QABase modules (`qabase-core`, `qabase-rest`, `qabase-web-ui`) in one go. Use this before a PR/merge to **see end‑to‑end quality in one dashboard**.

---

## Prerequisites
- **Java (JDK/JRE)** available on `PATH` → check: `java -version`
- **Maven** available on `PATH` → check: `mvn -v`
- **Allure CLI installed** → check: `allure --version`
  - macOS: `brew install allure`
  - Linux: `brew install allure` (Linuxbrew) or download the ZIP from GitHub releases
  - Windows: `choco install allurecommandline` or `scoop install allure`

> The script intentionally **does not** use the Maven Allure plugin. It relies on the **Allure CLI** for generation and serving.

---

## What the script does
1. **Builds & runs tests** for all three modules (unit + integration where configured).
2. **Validates results** exist under each module’s `target/allure-results`.
3. **Generates an aggregated static site** at `target/allure-reports-aggregate/` using the Allure CLI.
4. **Serves** all available result sets via `allure serve` (temporary local server).

---

## Quick Start
```bash
./scripts/allure-reports-serve.sh
```
This will:
- Run `mvn clean verify` across `qabase-core`, `qabase-rest`, and `qabase-web-ui`.
- Build a combined report.
- Open a local Allure server window/tab. Stop it with **Ctrl+C**.

---

## Options

| Flag | Description |
|---|---|
| `-h`, `--help` | Print the built‑in help text and exit. |

---

## Outputs
- Per‑module raw results: `qabase-*/target/allure-results/`
  (JSON + attachments created by tests)
- **Aggregated HTML site**: `target/allure-reports-aggregate/`
  Open `target/allure-reports-aggregate/index.html` directly, or rely on `allure serve`.

---

## Typical workflow

### Run a full, aggregated quality review locally
```bash
# from repo root
./scripts/allure-reports-serve.sh
```
- Validate that unit/integration tests across all modules are green.
- Share the generated static site folder if you need to attach artifacts to a ticket/PR.

### CI usage (static artifacts only)
```bash
# Example: in CI, you might skip the server and only generate static HTML
allure generate qabase-core/target/allure-results \
               qabase-rest/target/allure-results \
               qabase-web-ui/target/allure-results \
               --clean -o target/allure-reports-aggregate
```
Upload `target/allure-reports-aggregate/` as a pipeline artifact.

---

## Troubleshooting

**Allure CLI not found**
- Ensure `allure` is installed and on `PATH`. See **Prerequisites** above.

**No `allure-results` found**
- Make sure your tests actually generate Allure results. Some modules/profiles may disable results.
- Run module tests explicitly first to confirm results exist, e.g. `mvn -pl qabase-rest -am clean verify`.

**Script fails on Maven**
- Check `mvn -v`. Ensure Java and Maven are correctly installed.
- If your environment requires the Maven Wrapper, adjust the script or run:
  `./mvnw -pl qabase-core,qabase-rest,qabase-web-ui -am clean verify`

**Port already in use when serving**
- `allure serve` chooses a port automatically; if it clashes, stop other `allure serve` instances or generate static HTML and open it directly.

---

## FAQ

**Q: Can I aggregate only two modules?**
A: The script automatically **skips modules without results**. To limit scope, run tests for the modules you care about or modify the `MODULES` array in the script.

**Q: Where should I run the script from?**
A: Anywhere in the repo. The script resolves the repo root and `cd`s there before running Maven.

**Q: Does this replace per‑module reports?**
A: No. It complements them. For single‑module checks, open that module’s per‑module report or run only that module’s tests.

---

## Command reference (from `./scripts/allure-reports-serve.sh --help`)

```text
Usage:
  ./scripts/allure-reports-serve.sh [options]

Options:
  -h, --help   Show this help and exit
```

---

**Reminder:** Install **Allure CLI** first (`allure --version` should work). Then enjoy a unified view of your project’s test health.