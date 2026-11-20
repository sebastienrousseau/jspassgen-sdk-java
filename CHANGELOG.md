<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to the jspassgen suite's `0.0.x` versioning:
each release moves by `0.0.1`, in lockstep with the suite.

## [0.0.23] - Unreleased

In lockstep with the jspassgen suite's 0.0.23.

### Added

- `.github/CODEOWNERS`: the maintainer owns every file, so the main
  ruleset's required review has a code owner to request.
- Jazzer fuzz tests (`SecureRandomSourceFuzzTest`, test scope
  `com.code-intelligence:jazzer-junit` 0.30.0): with the argument and
  every random byte chosen by the fuzzer, `randomInt` stays in
  `[0, max)`, redraws exactly the draws at or above the largest
  multiple of `max` below 2^32 and returns the accepted draw modulo
  `max`; `bytes(n)` returns exactly the `n` drawn bytes; both reject a
  bound below 1 before drawing. Every `mvn test` replays the committed
  inputs, so CI runs them on Java 17 and 21; `JAZZER_FUZZ=1` fuzzes
  (`DEVELOPMENT.md`). This is the Jazzer integration OpenSSF Scorecard's
  Fuzzing check recognises for Java.

### Changed

- The version is 0.0.23 in `pom.xml`, `Jspassgen.VERSION`,
  `CITATION.cff`, the README install snippet and the `SECURITY.md` and
  `SUPPORT.md` support tables. The library's behaviour is unchanged.

## [0.0.22] - 2026-11-18

The repository's first version, at the first jspassgen suite version.

### Added

- `RandomSource`, the Java form of jspassgen's `RandomGeneratorPort`:
  `bytes(n)` and `randomInt(max)`, both rejecting values below 1 with
  `IllegalArgumentException`.
- `SecureRandomSource`, backed by `SecureRandom`, with an unbiased
  `randomInt` by rejection sampling over 32-bit draws.
- `Jspassgen.VERSION`.
- Unit tests: exact draws through a scripted generator, a chi-squared
  uniformity test, a modulo-bias test and the error cases; JaCoCo at
  100 % line and branch coverage.
- The build (`./mvnw` with pinned Maven and trusted checksums, PMD
  complexity ceilings, `make check`), CI on Java 17 and 21, CodeQL,
  DCO, docs lint, OSV-Scanner audit, OpenSSF Scorecard, Dependabot and
  the community and policy documents.
- The release workflow (`.github/workflows/release.yml`): on a signed
  annotated `v<version>` tag it checks the tag
  (`scripts/verify-release-tag`), runs `make check` and `make audit`,
  builds the jar, sources and Javadoc jars with `SHA256SUMS`, publishes
  them GPG-signed to Maven Central through the Central Portal after a
  maintainer approves the `maven-central` environment, and drafts the
  GitHub release. The `release` Maven profile and
  `make release-dry-run` hold the publication build; normal builds are
  unchanged.

### Fixed

- `make release-dry-run` starts from a clean `target/`. After
  `make check`, stale Javadoc pages were packed into the Javadoc jar,
  so the release run's checksums did not match the publish job's
  rebuild and the first `v0.0.22` run stopped before uploading.

### Not included

- Generators, validation and entropy: jspassgen's conformance kit
  shipped in jspassgen 0.0.21, but no generator is ported here yet, so
  the kit is not replayed.
- `conformance/`, `ci/core.ref`, examples, benchmarks, fuzz targets, the
  demo and the rendered manual.
