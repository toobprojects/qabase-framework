
# QABase Framework

A lightweight, modular **JVM test automation framework** that gets teams productive fast—without rebuilding the same plumbing on every project. QABase standardizes the boring bits (bootstrapping, test wiring, reporting, CI-ready structure) so you can focus on **real test logic**.

---

## Why QABase?

- **Zero-to-valuable quickly** – sensible defaults and a clean project layout.
- **Batteries included** – API testing with REST Assured, Web UI testing with Selenide, rich **Allure** reports.
- **Consistent across teams** – shared conventions and utilities reduce drift and review overhead.
- **Kotlin-first, Java-friendly** – concise Kotlin with excellent Java interop.
- **Multi-module** – pick what you need: Core, REST, or Web UI.

---

## Modules at a glance

- **qabase-core** – test utilities, configuration, logging, reporting helpers shared by other modules.
- **qabase-rest** – REST API test foundations built on **REST Assured** + Kotlin.
- **qabase-web-ui** – Web UI test foundations built on **Selenide** (Selenium) with Allure integration.

> Root coordinates (parent POM): `io.github.toobprojects:qabase-framework:1.1.1`

---

## Tech stack

- **Java 21+**, **Kotlin 2.x**
- **JUnit 5**
- **Spring Boot 3.4.x** (test utilities & wiring)
- **REST Assured** (API testing)
- **Selenide** (Web UI testing)
- **Kotlin Serialization**
- **Allure Reports**

---

## Use it as a dependency

Add only the modules you need.

### Maven

```xml
<dependency>
  <groupId>io.github.toobprojects</groupId>
  <artifactId>qabase-core</artifactId>
  <version>1.1.1</version>
</dependency>

<!-- API testing -->
<dependency>
  <groupId>io.github.toobprojects</groupId>
  <artifactId>qabase-rest</artifactId>
  <version>1.1.1</version>
</dependency>

<!-- Web UI testing -->
<dependency>
  <groupId>io.github.toobprojects</groupId>
  <artifactId>qabase-web-ui</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
  testImplementation("io.github.toobprojects:qabase-core:1.1.1")
  testImplementation("io.github.toobprojects:qabase-rest:1.1.1")   // API testing
  testImplementation("io.github.toobprojects:qabase-web-ui:1.1.1") // Web UI testing
}
```

---

## Working on the source (contributors)

### Clone & build

```bash
git clone https://github.com/toobprojects/qabase-framework.git
cd qabase-framework
mvn -q -DskipTests=false test
```

### Generate a local Allure report

```bash
mvn allure:serve
```

This spins up a local server with rich, navigable results (screenshots, attachments, steps).

## Developer Tools (Bash scripts)

QABase ships with a small set of **developer productivity tools** — plain Bash scripts that standardize common tasks like versioning, testing, releasing, and reporting. Use these to avoid repeating boilerplate commands and to keep your workflow consistent across machines and teams.

| Script | What it does | Guide |
|---|---|---|
| `bump-version.sh` | Bumps only the **project/module** versions (strict **SemVer**). Supports `--snapshot`, `--allow-dirty`, optional tag & push. Dependencies are **not** changed. | [Version Bump Script Guide](docs/qabase-version-bump-script-guide.md) |
| `release.sh` | Runs a Maven **release deploy** using the wrapper if present (`./mvnw`) with the `release` profile. Clear, colored logs and non‑zero exit on failure. | [Release Script Guide](docs/qabase-release-script-guide.md) |
| `test.sh` | Local **test runner**. Always executes from repo root, works with `mvnw` or `mvn`, supports `--module`, `--profile`, `--it` (failsafe), `--allure`, and `--jdk <path>`. | [Local Test Runner](docs/qabase-test-runner-script-guide.md) |
| `scripts/allure-reports-serve.sh` | Builds all modules and serves a **combined Allure report** via the Allure CLI. Useful for aggregated results across Core/REST/Web UI. | [Allure Reports Guide](docs/qabase-allure-reports-serve-script-guide.md) |

---

## Roadmap (short list)

- Docusaurus-powered documentation site (cookbooks, recipes, CI examples)
- Example tests for REST & Web UI (end-to-end flows)
- Maven Central badges & starter templates

---

## Contributing

1. Create a feature branch from `main`.
2. Add tests where practical.
3. Run `mvn -q test` and ensure Allure results look good.
4. Follow the existing module boundaries (Core vs REST vs Web UI).
5. Open a PR with a concise description and screenshots of reports where useful.

---

## License
QABase is licensed under the [Apache License 2.0](LICENSE).
