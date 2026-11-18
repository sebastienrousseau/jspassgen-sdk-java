<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

<p align="center">
  <img src="https://raw.githubusercontent.com/sebastienrousseau/jspassgen/main/.github/assets/logo.svg" alt="jspassgen logo" width="128" />
</p>

<h1 align="center">jspassgen-sdk-java</h1>

<p align="center">
  A native Java port of the jspassgen password and passphrase core, drawing randomness only from <code>SecureRandom</code>; version 0.0.22 holds the random source, and the generators are not ported yet.
</p>

<p align="center">
  <a href="https://github.com/sebastienrousseau/jspassgen-sdk-java/actions"><img src="https://img.shields.io/github/actions/workflow/status/sebastienrousseau/jspassgen-sdk-java/ci.yml?style=for-the-badge&logo=github" alt="Build" height="28" /></a>
  <a href="https://central.sonatype.com/artifact/com.jspassgen/jspassgen"><img src="https://img.shields.io/maven-central/v/com.jspassgen/jspassgen?style=for-the-badge&color=fc8d62&logo=apachemaven" alt="Maven Central version" height="28" /></a>
  <a href="DEVELOPMENT.md"><img src="https://img.shields.io/badge/docs-javadoc%20(local)-blue?style=for-the-badge&labelColor=555555&logo=readthedocs" alt="Docs" height="28" /></a>
  <a href="https://scorecard.dev/viewer/?uri=github.com/sebastienrousseau/jspassgen-sdk-java"><img src="https://img.shields.io/ossf-scorecard/github.com/sebastienrousseau/jspassgen-sdk-java?style=for-the-badge&label=OpenSSF%20Scorecard&logo=openssf" alt="OpenSSF Scorecard" height="28" /></a>
  <a href="LICENSE-APACHE"><img src="https://img.shields.io/badge/license-Apache--2.0%20OR%20MIT-blue.svg?style=for-the-badge" alt="License: Apache-2.0 OR MIT" height="28" /></a>
  <a href="https://github.com/sebastienrousseau/jspassgen-sdk-java/blob/main/docs/POLICIES.md"><img src="https://img.shields.io/badge/Java-17%2B-93450a.svg?style=for-the-badge&logo=openjdk" alt="Java 17 or newer" height="28" /></a>
</p>

---

## Contents

**Getting started**

- [Install](#install): the Maven Central dependency
- [Requirements](#requirements): Java floor, build toolchain, platforms
- [Quick Start](#quick-start): draw random bytes and unbiased integers from `SecureRandom`

**The jspassgen ecosystem**

- [The jspassgen ecosystem](#the-jspassgen-ecosystem): the core library, its tools and native SDKs, released in lockstep

**Library reference**

- [Capabilities at a glance](#capabilities-at-a-glance): the current surface by theme
- [Ecosystem comparison](#ecosystem-comparison): not yet; no generator to compare
- [Benchmarks](#benchmarks): none published yet
- [Features](#features): public API
- [Configuration](#configuration): choosing the `SecureRandom` algorithm
- [Examples](#examples): none yet

**Operational**

- [When not to use jspassgen-sdk-java](#when-not-to-use-jspassgen-sdk-java): limitations
- [Development](#development): make targets, CI
- [Security](#security): guarantees and supply chain
- [Documentation](#documentation): all reference docs
- [Stability guarantees](#stability-guarantees): versioning, output stability, minimum toolchain discipline
- [License](#license)

---

## Install

### As a Java library

The library is on Maven Central as `com.jspassgen:jspassgen`:

```xml
<dependency>
  <groupId>com.jspassgen</groupId>
  <artifactId>jspassgen</artifactId>
  <version>0.0.22</version>
</dependency>
```

This version holds the random source; the generators come in a later
version. Each GitHub release carries the jars and `SHA256SUMS`; there
are no distribution packages.

---

## Requirements

- Java 17 or newer at run time (`maven.compiler.release` 17).
- To build: a JDK 17 or newer. `./mvnw` downloads Maven 3.10.1 and checks
  its SHA-256, so no system Maven is needed; any Maven 3.9.0 or newer also
  builds the project.
- No runtime dependency beyond the JDK, and no network access at run time.

| Component | Minimum toolchain | Enforcement |
| :--- | :---: | :--- |
| `jspassgen-sdk-java` | Java 17 | CI builds and tests on Temurin 17 and 21 |

CI runs on Ubuntu only; other platforms are not tested.

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
below 1. Every generator ported later draws only through this interface,
so tests can replay a fixed sequence of draws.

---

## The jspassgen ecosystem

jspassgen is one core library and the tools, SDKs and site around it.
Every member is listed in
[`docs/ecosystem.json`](https://github.com/sebastienrousseau/jspassgen/blob/main/docs/ecosystem.json)
and, from the suite's first lockstep release, every member ships the same
version ([ADR 0001](https://github.com/sebastienrousseau/jspassgen/blob/main/docs/adr/0001-ecosystem-lockstep.md)).
The JavaScript tools depend on the core and add no generation logic of
their own; each SDK is a native port that is checked against the core's
conformance kit as its generators land.

| Component | Purpose | Use case |
| :--- | :--- | :--- |
| [`jspassgen`](https://github.com/sebastienrousseau/jspassgen) | Password and passphrase core, Node.js library, CLI, web UI and workers; source of the conformance kit | Programs that generate secrets |
| [`jspassgen-cli`](https://github.com/sebastienrousseau/jspassgen-cli) | Command-line generator built on the core | Passwords in a terminal or a script |
| [`jspassgen-mcp`](https://github.com/sebastienrousseau/jspassgen-mcp) | Local stdio MCP server | AI agents that need a generated secret |
| [`jspassgen-lsp`](https://github.com/sebastienrousseau/jspassgen-lsp) | Language server for weak literal secrets | Editors checking configuration and `.env` files |
| [`jspassgen-sdk-python`](https://github.com/sebastienrousseau/jspassgen-sdk-python) | Native port on the `secrets` module | Python programs |
| [`jspassgen-sdk-rust`](https://github.com/sebastienrousseau/jspassgen-sdk-rust) | Native port on the OS CSPRNG | Rust programs |
| **`jspassgen-sdk-java`** | Native port on `SecureRandom` | JVM programs |
| [`jspassgen-sdk-dotnet`](https://github.com/sebastienrousseau/jspassgen-sdk-dotnet) | Native port on `RandomNumberGenerator` | .NET programs |
| [`jspassgen-sdk-cpp`](https://github.com/sebastienrousseau/jspassgen-sdk-cpp) | Native C++17 port on the OS CSPRNG | C++ programs |
| [`jspassgen.github.io`](https://github.com/sebastienrousseau/jspassgen.github.io) | Source of [jspassgen.com](https://jspassgen.com) | Evaluating the suite |

Every member ships at 0.0.22, the suite's first lockstep release: the
core, CLI, MCP and LSP servers on npm, the Python, Rust, Java and .NET
SDKs on PyPI, crates.io, Maven Central and NuGet, and the C++ SDK and
the site as GitHub releases. The table gives each member's purpose, and
each README's Capabilities at a glance says what works today.

---

## Capabilities at a glance

| Area | Capability | Status |
| :--- | :--- | :--- |
| Randomness | `RandomSource` port and `SecureRandomSource` (bytes, unbiased integers by rejection sampling) | Available in 0.0.22 |
| Generators | Password types from jspassgen's `PASSWORD_TYPES`, validation, entropy, error codes | Not ported yet |
| Conformance | jspassgen's conformance kit (draw vectors, errors, wordlists) replayed in CI | Not wired yet: the kit is in jspassgen, but this port has no generator to replay it against |
| Distribution | Maven Central, GitHub releases | Released in 0.0.22 |

---

## Ecosystem comparison

Not applicable yet. With no generator ported there is nothing to compare
against other Java password libraries; a comparison will be written from
evidence once the port passes the conformance kit.

| Project | Native (no network) | CSPRNG only | Proven against jspassgen |
| :--- | :---: | :---: | :---: |
| **jspassgen-sdk-java** | Yes | Yes (`SecureRandom`) | Not yet |

---

## Benchmarks

None published yet. The only code is a thin wrapper over `SecureRandom`,
no measurement has been made, and the repository has no benchmark target.

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
To choose the algorithm, pass your own instance (a null one throws
`NullPointerException`):

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

## When not to use jspassgen-sdk-java

- You need to generate passwords or passphrases today: this version has
  no generator. Use [jspassgen](https://github.com/sebastienrousseau/jspassgen)
  on Node.js, or `SecureRandom` directly.
- You need output that matches jspassgen byte for byte: that is the goal,
  but it is not proven until a ported generator passes the conformance
  kit.

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
  recorded in `.mvn/checksums/`. GitHub Actions are pinned by commit SHA,
  and CodeQL, OSV-Scanner and OpenSSF Scorecard run in CI.

Report vulnerabilities according to [`SECURITY.md`](SECURITY.md).

---

## Documentation

- [Developer documentation](DEVELOPMENT.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Engineering policies](docs/POLICIES.md)
- [Changelog](CHANGELOG.md)
- [Contributing](CONTRIBUTING.md)
- [Ecosystem map](https://github.com/sebastienrousseau/jspassgen/blob/main/docs/ecosystem.json)

No user manual or hosted API reference is published yet; `make javadoc`
builds the API reference into `target/reports/apidocs`.

---

## Stability guarantees

- Versions are `0.0.x` and move by `0.0.1`, in lockstep with the jspassgen
  suite. While pre-1.0, any release may change the API; the changelog
  says what changed.
- Output stability: once generators are ported, a change to what they
  produce for a given sequence of random draws is a breaking change, even
  when no signature moves, and lands with the matching change in
  jspassgen in the same suite version.
- Minimum toolchain: Java 17. It is raised only in a release whose
  changelog says so, and the reason is recorded in
  [docs/POLICIES.md](docs/POLICIES.md).

---

## License

Licensed under either [Apache License 2.0](LICENSE-APACHE) or
[MIT](LICENSE-MIT), at your option.
