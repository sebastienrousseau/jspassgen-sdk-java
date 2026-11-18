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
| Release / Check the tag, run the gate, build the jars (on a `v*` tag) | `scripts/verify-release-tag v<version>` and `make check audit release-dry-run` | The tag (see [Release model](#release-model)), every gate, the release build |

Not present yet, because they would have nothing to run: a conformance
job (the kit ships in jspassgen 0.0.21, but no generator is ported here
to run it), a fuzz workflow (no parser or generator to
fuzz) and a docs deployment (no rendered manual).

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

`.github/workflows/release.yml` releases a pushed `v<version>` tag to
Maven Central. Nothing has been released yet; the first release is the
suite's first lockstep version, 0.0.22.

1. On `feat/v<version>`: move the version in `pom.xml` and its
   restatements, and date the `CHANGELOG.md` heading. Merge the release
   pull request into `main` once CI is green.
2. Tag `main` with a signed annotated tag, check it, and push it:
   `git tag -s v<version> -m "jspassgen-sdk-java v<version>"`, then
   `git verify-tag v<version>` (`KEYS.asc`) and
   `scripts/verify-release-tag v<version>`.
3. The `check` job runs `scripts/verify-release-tag`, which refuses a
   tag that is not annotated, not signed, not on the checked-out commit,
   whose message is not exactly `jspassgen-sdk-java v<version>`, or
   whose version disagrees with `pom.xml` or any file that restates it.
   It then runs `make check` and `make audit`, builds the jar, sources
   and Javadoc jars with `make release-dry-run` (unsigned, nothing
   uploaded), and writes `SHA256SUMS`. CI cannot verify the signature
   itself; step 2 does.
4. The `publish-central` job waits for a maintainer to approve the
   `maven-central` environment. It skips a version already on Maven
   Central; otherwise it rebuilds the jars, checks them against
   `SHA256SUMS` (the build is reproducible through
   `project.build.outputTimestamp`), signs them with GPG and uploads
   them through the Central Portal plugin (`-Prelease deploy`), then
   waits until the version is on repo1.maven.org.
5. The `github-release` job drafts the GitHub release
   `jspassgen-sdk-java <version>` with the jars and `SHA256SUMS`. The
   maintainer writes the Highlights and publishes the draft.

The `release` Maven profile holds everything publication needs (the
sources, Javadoc, GPG and Central Portal plugins), so `make check` and
normal builds do not touch it and need no key. `make release-dry-run`
runs it locally: it builds the Central bundle in
`target/central-publishing/` and uploads nothing; set `GPG_KEYNAME` to
sign as well.

Maven Central has no OIDC trusted publishing, so the `maven-central`
environment holds four secrets: `CENTRAL_TOKEN_USERNAME` and
`CENTRAL_TOKEN_PASSWORD` (a Central Portal user token),
`GPG_PRIVATE_KEY` (the ASCII-armoured OpenPGP key that signs the
artifacts) and `GPG_PASSPHRASE`. Before the first release the
maintainer verifies the `com.jspassgen` namespace at Sonatype Central
(a DNS TXT record on jspassgen.com), generates the user token, and
publishes the OpenPGP public key to a keyserver Central reads (for
example keys.openpgp.org). That key signs Maven artifacts only; commits
and tags stay SSH-signed (`KEYS.asc`).
