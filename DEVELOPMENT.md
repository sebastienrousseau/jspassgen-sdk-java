<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Development

The single entry point for working on `jspassgen-sdk-java`: the
toolchain, every CI gate reproduced locally, the test layout and the
release model. [CONTRIBUTING.md](CONTRIBUTING.md) covers the
pull-request etiquette.

## Toolchain

| Tool | Version | Why |
| :--- | :--- | :--- |
| JDK | 17 or newer (CI: Temurin 17 and 21; `mise.toml`: 21) | The build floor is `maven.compiler.release` 17 |
| Maven | 3.10.1 via `./mvnw` (SHA-256 checked) | Any 3.9.0+ works |
| GNU Make | any | The task runner |
| Optional | `osv-scanner`, `reuse`, `codespell`, `lychee`, Node.js (for `npx markdownlint-cli2`), `actionlint` | The audit, licence, spelling, link, Markdown and workflow gates |

## Every CI gate, locally

| CI job | Local command | What it checks |
| :--- | :--- | :--- |
| CI / Lint and format | `scripts/verify-release-versions` and `make format-check size lint` | Versions agree; no tabs, trailing spaces or CRLF; library within 100 columns; 500 lines per file; `javac -Xlint:all -Werror`; PMD ceilings |
| CI / Build, unit tests and coverage (Java 17, 21) | `make check` | `mvn verify`: unit tests, JaCoCo 100 % line and branch, PMD; Javadoc with doclint |
| Dependency audit | `make audit` | CycloneDX SBOM scanned by OSV-Scanner |
| Docs lint | `make docs-lint` | markdownlint, codespell, REUSE, lychee (offline) |
| CodeQL, Scorecard, DCO | (GitHub only) | Static analysis, supply-chain posture, `Signed-off-by` on every commit |

Not present yet, because they would have nothing to run: a conformance
job (the kit ships in jspassgen 0.0.21, but no generator is ported here
to run it), a fuzz workflow (no parser or generator to
fuzz), a docs deployment (no rendered manual) and a release workflow
(nothing to publish until the namespace is verified).

## Tests

| Location | What |
| :--- | :--- |
| `SecureRandomSourceTest` | Range and error cases, exact draws through `ScriptedRandom`, the rejection path, chi-squared uniformity (p = 1e-6) and a modulo-bias check |
| `ScriptedRandom` | A `SecureRandom` that returns scripted bytes, for exact draws |

### Coverage and complexity

JaCoCo fails `mvn verify` below 100 % line and 100 % branch coverage of
`src/main`, with no exclusions. PMD enforces cyclomatic 10, cognitive 15
and 60 statements per method on main and test code; `make size`
enforces 500 lines per file. Halstead difficulty (ceiling 30) has no
maintained Java tool in the build and is not measured.

## Release model

- Versions are `0.0.N` and move by `0.0.1` in lockstep with the jspassgen
  suite; `pom.xml` is the source of truth.
- Work happens on `feat/v<next-version>`, the only pull request into
  `main`.
- No release workflow exists yet. Before the first release: verify the
  `com.jspassgen` namespace at Sonatype Central (DNS record on
  jspassgen.com), add the release workflow with a dry run, and publish
  the signing key used for Maven artifacts.
