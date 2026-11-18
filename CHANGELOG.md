<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to the jspassgen suite's `0.0.x` versioning:
each release moves by `0.0.1`, in lockstep with the suite.

## [0.0.22] - Unreleased

The repository's first version, at the planned first jspassgen suite
version. Nothing is published.

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

### Not included

- Generators, validation and entropy: jspassgen's conformance kit
  shipped in jspassgen 0.0.21, but no generator is ported here yet, so
  the kit is not replayed.
- `conformance/`, `ci/core.ref`, examples, benchmarks, fuzz targets, the
  demo and the rendered manual.
- Publication to Maven Central: the `com.jspassgen` namespace is not
  verified yet.
