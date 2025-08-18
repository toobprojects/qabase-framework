
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

> Root coordinates (parent POM): `io.github.toobprojects:qabase-framework:1.0.1`

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
  <version>1.0.1</version>
</dependency>

<!-- API testing -->
<dependency>
  <groupId>io.github.toobprojects</groupId>
  <artifactId>qabase-rest</artifactId>
  <version>1.0.1</version>
</dependency>

<!-- Web UI testing -->
<dependency>
  <groupId>io.github.toobprojects</groupId>
  <artifactId>qabase-web-ui</artifactId>
  <version>1.0.1</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
  testImplementation("io.github.toobprojects:qabase-core:1.0.1")
  testImplementation("io.github.toobprojects:qabase-rest:1.0.1")   // API testing
  testImplementation("io.github.toobprojects:qabase-web-ui:1.0.1") // Web UI testing
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

### Bump the version (SemVer)

We keep **Semantic Versioning**. To change the **project/module versions** safely (without touching dependency versions), use the script documented here:

- **Version bump script & usage → [docs/%E2%89%A1%C6%92%C3%B4%C2%AA%20QABase%20%CE%93%C3%87%C3%B6%20Version%20Bump%20Script%20%28%60bump-version.sh%60%29.md](docs/%E2%89%A1%C6%92%C3%B4%C2%AA%20QABase%20%CE%93%C3%87%C3%B6%20Version%20Bump%20Script%20%28%60bump-version.sh%60%29.md)**

> TL;DR: run from repo root, e.g. `./bump-version.sh 1.1.0 --tag --push`

---

## Roadmap (short list)

- Docusaurus-powered documentation site (cookbooks, recipes, CI examples)
- Example projects for REST & Web UI (end-to-end flows)
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

Apache License 2.0 – see `LICENSE`.
