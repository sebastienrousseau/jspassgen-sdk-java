<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

<p align="center">
  <img src="https://raw.githubusercontent.com/sebastienrousseau/jspassgen/master/.github/assets/logo.svg" alt="jspassgen logo" width="128" />
</p>

<h1 align="center">jspassgen SDK for Java</h1>

<p align="center">
  A planned native Java port of the jspassgen password and passphrase core, drawing randomness only from <code>SecureRandom</code>. Version 0.0.22 ships the random source; the generators are not ported yet.
</p>

<p align="center">
  <a href="https://github.com/sebastienrousseau/jspassgen-sdk-java/actions"><img src="https://img.shields.io/github/actions/workflow/status/sebastienrousseau/jspassgen-sdk-java/ci.yml?style=for-the-badge&logo=github" alt="Build" height="28" /></a>
  <a href="docs/POLICIES.md#publication"><img src="https://img.shields.io/badge/maven%20central-not%20published-lightgrey?style=for-the-badge&color=fc8d62&logo=apachemaven" alt="Registry: not published" height="28" /></a>
  <a href="DEVELOPMENT.md"><img src="https://img.shields.io/badge/docs-javadoc%20(local)-blue?style=for-the-badge&labelColor=555555&logo=readthedocs" alt="Docs" height="28" /></a>
  <a href="https://scorecard.dev/viewer/?uri=github.com/sebastienrousseau/jspassgen-sdk-java"><img src="https://img.shields.io/ossf-scorecard/github.com/sebastienrousseau/jspassgen-sdk-java?style=for-the-badge&label=OpenSSF%20Scorecard&logo=openssf" alt="OpenSSF Scorecard" height="28" /></a>
  <a href="LICENSE-APACHE"><img src="https://img.shields.io/badge/license-Apache--2.0%20OR%20MIT-blue.svg?style=for-the-badge" alt="License: Apache-2.0 OR MIT" height="28" /></a>
  <a href="https://github.com/sebastienrousseau/jspassgen-sdk-java/blob/main/docs/POLICIES.md"><img src="https://img.shields.io/badge/Java-17%2B-93450a.svg?style=for-the-badge&logo=openjdk" alt="Java 17 or newer" height="28" /></a>
</p>

<!-- No demo GIF yet: this version has no generator, so there is no real
     output worth recording. The VHS tape and `make demo` arrive with the
     first ported generator. -->

---

## Contents

**Getting started**

- [Install](#install): build from source into your local Maven repository (not on Maven Central yet)
- [Requirements](#requirements): toolchain floor, platforms
- [Quick Start](#quick-start): draw random bytes and unbiased integers from `SecureRandom`

**The jspassgen SDK for Java ecosystem**

- [The jspassgen SDK for Java ecosystem](#the-jspassgen-sdk-for-java-ecosystem): the ten jspassgen suite members

**Library reference**

- [Capabilities at a glance](#capabilities-at-a-glance): the current surface by theme
- [Ecosystem comparison](#ecosystem-comparison): not yet, nothing to compare
- [Benchmarks](#benchmarks): not yet, nothing to measure
- [Features](#features): module-level capability list
- [Configuration](#configuration): core options
- [Examples](#examples): runnable example index

**Operational**

- [When not to use jspassgen SDK for Java](#when-not-to-use-jspassgen-sdk-for-java): limitations
- [Development](#development): make targets, CI
- [Security](#security): guarantees and compliance
- [Documentation](#documentation): all reference docs
- [Stability guarantees](#stability-guarantees): SemVer axis, output stability, minimum toolchain discipline
- [License](#license)

---

## Install

### As a Java library

Nothing is published yet. The proposed coordinates are
`com.jspassgen:jspassgen`; the `com.jspassgen` namespace needs DNS
verification of jspassgen.com at Sonatype Central before the first
publication. Until then, build it into your local Maven repository:

```sh
git clone https://github.com/sebastienrousseau/jspassgen-sdk-java.git
cd jspassgen-sdk-java
./mvnw -B install
```

and depend on it from there:

```xml
<dependency>
  <groupId>com.jspassgen</groupId>
  <artifactId>jspassgen</artifactId>
  <version>0.0.22</version>
</dependency>
```

There are no pre-built jars, GitHub releases or distribution packages
yet.

---

## Requirements

- Java 17 or newer at run time (`maven.compiler.release` 17). CI builds
  and tests on Temurin 17 and 21.
- To build: a JDK 17 or newer. `./mvnw` downloads Maven 3.10.1 and checks
  its SHA-256; no system Maven is needed.
- No runtime dependency beyond the JDK, and no network access.

---

## Quick Start

```java
import com.jspassgen.RandomSource;
import com.jspassgen.SecureRandomSource;

public final class QuickStart {
  public static void main(String[] args) {
    RandomSource random = new SecureRandomSource();
    byte[] salt = random.bytes(16);   // 16 bytes from SecureRandom
    int die = random.randomInt(6);    // uniform in [0, 6), never a bare modulo
    System.out.println(salt.length + " bytes, die " + (die + 1));
  }
}
```

`RandomSource` is the Java form of jspassgen's `RandomGeneratorPort`:
`bytes(n)` matches `generateRandomBytes` and `randomInt(max)` matches
`generateRandomInt`. Both throw `IllegalArgumentException` for a value
below 1. Every generator ported later draws only through this
interface, so tests can replay a fixed sequence of draws.

---

## The jspassgen SDK for Java ecosystem

This repository is one member of the jspassgen suite: ten repositories
that will release the same `0.0.x` version together
([jspassgen ADR 0001](https://github.com/sebastienrousseau/jspassgen/blob/main/docs/adr/0001-ecosystem-lockstep.md)).
Every member except jspassgen itself is planned; none of them has shipped
as part of the suite yet.

| Component | Purpose | Use case |
| :--- | :--- | :--- |
| [jspassgen](https://github.com/sebastienrousseau/jspassgen) | Zero-dependency JavaScript core, Node.js library, web UI and workers; source of the conformance kit | The reference implementation every port must match |
| jspassgen-cli, jspassgen-mcp, jspassgen-lsp | Command line, local stdio MCP server, language server | Planned JavaScript tools built on the core |
| jspassgen-sdk-python, jspassgen-sdk-rust, jspassgen-sdk-dotnet, jspassgen-sdk-cpp | Native ports on each platform's CSPRNG | Planned sibling ports |
| **jspassgen-sdk-java** (this repository) | Native Java port on `SecureRandom` | Password and passphrase generation inside the JVM, with no network call |
| jspassgen.github.io | jspassgen.com, product and ecosystem pages | Planned site member |

---

## Capabilities at a glance

| Area | Capability | Status |
| :--- | :--- | :--- |
| Randomness | `RandomSource` port and `SecureRandomSource` (bytes, unbiased integers by rejection sampling) | Available in 0.0.22 |
| Generators | Password types from jspassgen's `PASSWORD_TYPES`, validation, entropy, error codes | Not ported yet |
| Conformance | jspassgen's conformance kit (draw tapes, errors, wordlist) replayed in CI | Not available: the kit does not exist in jspassgen yet |
| Distribution | Maven Central, GitHub releases with `SHA256SUMS` | Not published |

---

## Ecosystem comparison

Not applicable yet. With no generator ported there is nothing to compare
against other Java password libraries; a comparison will be written,
from evidence, once the port passes the conformance kit.

| Project | Native (no network) | CSPRNG only | Proven against jspassgen |
| :--- | :---: | :---: | :---: |
| **jspassgen SDK for Java** | Yes | Yes (`SecureRandom`) | Not yet |

---

## Benchmarks

None yet. The only code is a thin wrapper over `SecureRandom`, and no
measurement has been made, so no number is published.

| Scenario | Result | Environment |
| :--- | ---: | :--- |
| None measured | n/a | n/a |

---

## Features

- `com.jspassgen.RandomSource`: the random port, `bytes(int n)` and
  `randomInt(int max)`.
- `com.jspassgen.SecureRandomSource`: the `SecureRandom` implementation.
  `randomInt` draws 32 bits and rejects values at or above the largest
  multiple of `max` below 2<sup>32</sup>, so every result is equally
  likely.
- `com.jspassgen.Jspassgen.VERSION`: the library version, `0.0.22`.

---

## Configuration

`SecureRandomSource()` uses `new SecureRandom()`, the platform default.
To choose the algorithm, pass your own instance:

```java
RandomSource random = new SecureRandomSource(SecureRandom.getInstance("DRBG"));
```

There is no other option, no configuration file and no environment
variable.

---

## Examples

None yet. The `examples/` directory arrives with the first ported
generator; the [Quick Start](#quick-start) shows the whole current
surface.

---

## When not to use jspassgen SDK for Java

- You need to generate passwords or passphrases today: this version has
  no generator. Use [jspassgen](https://github.com/sebastienrousseau/jspassgen)
  on Node.js, or `SecureRandom` directly.
- You need an artifact from Maven Central: nothing is published.
- You need output that matches jspassgen byte for byte: that is the goal,
  but it is not proven until the conformance kit exists and passes.

---

## Development

```bash
make check     # versions, format, size, mvn verify (tests, 100 % coverage, PMD), Javadoc
make lint      # javac -Xlint:all -Werror and PMD complexity ceilings
make audit     # CycloneDX SBOM scanned by OSV-Scanner
make docs-lint # markdownlint, codespell, REUSE, lychee (offline)
make help      # every target
```

`make check` is the one gate; CI runs it on Java 17 and 21.
[DEVELOPMENT.md](DEVELOPMENT.md) maps every CI job to its local command.

---

## Security

- Randomness comes only from `java.security.SecureRandom`; nothing uses
  `java.util.Random` or `Math.random()`.
- `randomInt` uses rejection sampling, never a modulo of raw bytes, and a
  test checks the result against a case where a bare modulo would be
  visibly biased.
- No network call, no telemetry, no logging, no file access.
- Every Maven artifact the build resolves is checked against the SHA-256
  recorded in `.mvn/checksums/`; GitHub Actions are pinned by commit SHA;
  CodeQL, OSV-Scanner and OpenSSF Scorecard run in CI.

Report vulnerabilities according to [`SECURITY.md`](SECURITY.md).

---

## Documentation

- User manual: not published yet (no rendered manual or Pages site).
- API reference: `make javadoc` builds it into `target/reports/apidocs`;
  it is not hosted yet.
- [DEVELOPMENT.md](DEVELOPMENT.md): toolchain, every CI gate, release
  model.
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): how the port is shaped.
- [docs/POLICIES.md](docs/POLICIES.md): toolchain, versioning,
  dependencies, publication.
- Ecosystem map: [jspassgen `docs/ecosystem.json`](https://github.com/sebastienrousseau/jspassgen/blob/main/docs/ecosystem.json).

---

## Stability guarantees

- Versions are `0.0.x` and move by `0.0.1`, in lockstep with the jspassgen
  suite. While pre-1.0, any release may change the API; the CHANGELOG
  says what changed.
- Output stability: once generators are ported, a change to what they
  produce for a given sequence of random draws is a breaking change, even
  when no signature moves, and lands with the matching change in
  jspassgen in the same suite version.
- Minimum toolchain: Java 17. It is raised only in a release whose
  CHANGELOG says so, and the reason is recorded in
  [docs/POLICIES.md](docs/POLICIES.md).

---

## License

Dual licensed under the [Apache License, Version 2.0](LICENSE-APACHE) or
the [MIT licence](LICENSE-MIT), at your option. The SPDX expression is
`Apache-2.0 OR MIT`.
