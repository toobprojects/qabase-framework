# QABase – Allure Reports Script

## 🧪 Allure Reports (Tests + HTML)

We provide a helper script to run tests with the Allure Maven profile enabled and generate a single aggregated HTML report at the project root.

### Usage

```bash
./allure-reports.sh                # run tests + build report + auto-open in browser
./allure-reports.sh --no-open      # run tests + build report (don’t auto-open)
./allure-reports.sh --serve        # run tests + build report + serve via Allure CLI
./allure-reports.sh --mvn-args "-Dtest=*IT"  # pass extra Maven args
./allure-reports.sh --help         # show usage/help
```

### What it does
1. Runs `mvn clean verify -Pallure-reports` 🧪
   - Executes all tests across the reactor.
   - Writes Allure result JSONs into each module’s `target/allure-results/`.
2. Runs `mvn -N -Pallure-reports -DskipTests allure:report` 📊
   - Copies all submodules’ results into the root `target/allure-results/`.
   - Generates the aggregated HTML report at `target/allure-reports/` (root).
3. Opens the report automatically in your default browser 🌐 (unless `--no-open` is used).

### Notes
- The `--serve` flag requires the Allure CLI installed and on your `PATH`.
- By default, the script will open the report in your browser when it’s ready.
- The parent POM’s `allure-reports` profile ensures consumers don’t need to add any extra dependencies.