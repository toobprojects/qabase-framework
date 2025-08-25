# 🚀 QABase — Release Script Guide (`release.sh`)

A **safe, opinionated helper** for publishing Maven releases with a consistent profile. It wraps Maven commands with clear logging and basic error handling.

---

## 🧰 Prerequisites
- **Maven** (`mvn`) or **Maven Wrapper** (`mvnw`) available on `PATH`.
- Run **from the project root** (must contain `pom.xml`).
- A configured **release profile** in your Maven `pom.xml` (e.g., `<profile>release</profile>`).
- (Recommended) Inside a **Git repo** so release commits and tags can be tracked.

---

## 🔎 What it does
- Prints **clear, colored log messages** for visibility.
- Uses **Maven Wrapper** (`./mvnw`) if available, otherwise falls back to `mvn`.
- Runs: `mvn -U -ntp clean deploy -P release`
- Fails fast with descriptive error output if Maven fails.

---

## 🚦 Usage
Run the script directly from the project root:

`./release.sh`

No arguments or flags required.

---

## ✅ Example Run
Successful release:

    ➜ ./release.sh
    [INFO] Starting Maven release build...
    [INFO] Running ./mvnw -U -ntp clean deploy -P release
    [SUCCESS] Release build completed successfully!

If Maven fails:

    ➜ ./release.sh
    [INFO] Starting Maven release build...
    [ERROR] Release build failed with status code 1

---

## 📝 Logging & Status Codes
- **[INFO]** → Informational steps (start, commands being run).
- **[SUCCESS]** → Build and deploy completed successfully.
- **[WARN]** → Non-critical hints (not commonly used yet).
- **[ERROR]** → Build or deploy failure, script exits with the Maven exit code.

---

## 🧯 Safety Notes
- Always run from the **root of the project**.
- Ensure you’re on the correct branch before running (commonly `main`/`master`).
- Confirm your Maven `settings.xml` has the right **repository credentials**.
- Script runs with strict error checking (`set -euo pipefail` via Bash defaults in CI recommended).

---

## 💡 Tips
- Pair this with the [`bump-version.sh`](./qabase-version-bump-script-guide.md) script:
    1. **Bump version** → `./bump-version.sh 1.2.3 --tag --push`
    2. **Release build** → `./release.sh`
- Use in CI pipelines for automated release flows.
- Keep the `release` Maven profile lightweight and only for publishing.