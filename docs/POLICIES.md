<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Policies

## Toolchain

- The language floor is **Java 17** (`maven.compiler.release` 17). CI
  builds and tests on Temurin 17 and 21; `mise.toml` pins JDK 21 for
  local work.
- The floor moves only in a release whose changelog says so; the
  maintainer decides when, and the reason is recorded here.
- `./mvnw` runs Maven 3.10.1, checked against its SHA-256. Any Maven
  3.9.0 or newer also builds the project. Every plugin is pinned in
  `pom.xml`, and the enforcer fails the build on an unpinned plugin or a
  dynamic dependency version.

## Versioning

- Versions are `0.0.x` and move by `0.0.1`, in lockstep with the
  jspassgen suite. `pom.xml` is the single source of truth;
  `scripts/verify-release-versions` checks every other file.

## Dependencies

- No runtime dependency. JUnit Jupiter is test-only; JaCoCo, PMD, the
  enforcer, Javadoc and CycloneDX are build plugins. Dependabot proposes
  updates weekly.
- Maven verifies every resolved artifact against the SHA-256 recorded in
  `.mvn/checksums/` and fails on one that is missing or different.
  `make lock` re-records the file.
- `make audit` scans the CycloneDX SBOM with OSV-Scanner.

## Publication

- Nothing is published. The proposed Maven coordinates are
  `com.jspassgen:jspassgen`.
- The `com.jspassgen` namespace must be verified at Sonatype Central by
  a DNS record on jspassgen.com before the first publication, which the
  maintainer performs.
