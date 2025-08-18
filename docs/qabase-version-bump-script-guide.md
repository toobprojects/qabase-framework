# 📦 QABase — Version Bump Script Guide (`bump-version.sh`)

---

A safe, opinionated helper for updating **project/module versions** across a Maven multi-module repo.
It **does not** touch dependency versions.

---

## 🧰 Prerequisites

- **Maven** (`mvn`) available on `PATH`.
- Run **from the project root** (must contain `pom.xml`).
- (Recommended) Inside a **Git repo** so changes can be committed and tagged.

---

## 🔎 What it does

- Validates the version you pass in (strict **SemVer**).
- Accepts optional leading `v` (e.g., `v1.2.3` → `1.2.3`).
- Optionally appends `-SNAPSHOT` (via `--snapshot`).
- Uses **Maven Versions Plugin** to update the **parent and all modules**:
  - `versions:set -DnewVersion=... -DprocessAllModules=true -DgenerateBackupPoms=false`
- Leaves **dependency versions** untouched.
- Optionally commits, tags (`v<version>`), and pushes via Git.

---

## 🚦Usage

./bump-version.sh <newVersion> [--snapshot] [--allow-dirty] [--tag] [--push]
### Arguments & Flags

- `<newVersion>`: **SemVer** `MAJOR.MINOR.PATCH`
  Examples: `1.2.3`, `1.2.3-rc.1`, `v2.0.0` (leading `v` is stripped)
- `--snapshot`: Append `-SNAPSHOT` to the final version.
- `--allow-dirty`: Skip the clean-working-tree check (use with care).
- `--tag`: Create an annotated Git tag `v<version>`.
- `--push`: Push the commit and tags (`git push && git push --tags`).
- `-h | --help`: Show usage.

> ❗ Do **not** include `-SNAPSHOT` in `<newVersion>`; use `--snapshot` instead.

---

## ✅ Examples

Set a release version:
`./bump-version.sh 0.3.0`

Set a snapshot version:
`./bump-version.sh 0.4.0 --snapshot`

Resulting version: 0.4.0-SNAPSHOT

Tag (but don’t push yet):
`./bump-version.sh 1.0.0 --tag`

Creates tag v1.0.0 locally

Tag and push:
`./bump-version.sh 1.1.0 --tag --push`

Accept a leading "v":
`./bump-version.sh v2.0.0 --tag`

Uses 2.0.0 and creates tag v2.0.0

Work in-progress (allow dirty tree):
`./bump-version.sh 0.5.0 --allow-dirty`

---

## 🧪 Validation Rules

- Strict SemVer: `MAJOR.MINOR.PATCH` with optional `-prerelease` and `+build` metadata.
- Rejects values starting with `-` (guards against passing flags as the version).
- Rejects versions ending with `-SNAPSHOT` (use `--snapshot`).

---

## 📝 Git Behavior

- If in a Git repo, the script stages and commits `pom.xml` changes:
  - commit message: `chore: bump project version to <version>`
- `--tag` adds annotated tag `v<version>`.
- `--push` pushes the commit and tags.

> Because `generateBackupPoms=false`, **rollback** is via Git:
>
> - `git reset --hard HEAD~1` (undo last commit)
> - Or manually edit and recommit

---

## 🧯 Safety Checks & Exit Conditions

- Fails if:
  - `mvn` is not found on `PATH`.
  - No `pom.xml` in the current directory.
  - Working tree is **dirty** (unless `--allow-dirty`).
  - Version is invalid / misformatted.
- Script runs with `set -euo pipefail` for strict error handling.

---

## 💡 Tips

- Always run from the repo **root**.
- Use `--snapshot` during development; use plain `x.y.z` for releases.
- Pair `--tag --push` on release day to publish version control metadata consistently.
