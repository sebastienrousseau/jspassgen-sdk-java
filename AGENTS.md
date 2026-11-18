<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# AGENTS.md

`jspassgen-sdk-java` is the planned native Java port of jspassgen's
core (`packages/core` in
[jspassgen](https://github.com/sebastienrousseau/jspassgen)): the same
password types, validation, entropy and error codes, generated inside
the JVM from `SecureRandom`, never over a network. It is a member of the
jspassgen suite ([ADR 0001](https://github.com/sebastienrousseau/jspassgen/blob/main/docs/adr/0001-ecosystem-lockstep.md)).
Today it holds only the random port (`RandomSource`) and its
`SecureRandom` implementation; generators are ported once jspassgen's
conformance kit exists. These are the rules an automated change is most
likely to break; humans follow them too. Read
[CONTRIBUTING.md](CONTRIBUTING.md) first.

## Versioning

- The version is `0.0.x` and moves **strictly by `0.0.1`**, in lockstep
  with the jspassgen suite. Never propose a `0.1.0` or `1.0` jump, and
  never bump it as a side effect of a change.
- `pom.xml` is the single source of truth. It is restated in
  `Jspassgen.VERSION`, `CITATION.cff`, the `SECURITY.md` and
  `SUPPORT.md` tables, every install snippet and a `## [0.0.N]` heading
  in `CHANGELOG.md`; `scripts/verify-release-versions` fails on any
  mismatch.
- Work for the next version happens on `feat/v<next-version>`, the only
  pull request into `main`; other branches merge into it, never into
  `main`.

## Native port, local only

- Randomness comes only from `SecureRandom`, through `RandomSource`.
  Never `java.util.Random`, `Math.random()`, `ThreadLocalRandom` or a
  modulo of raw bytes; `randomInt` is unbiased by rejection sampling.
- No network call, no telemetry, no logging, no file access, no runtime
  dependency beyond the JDK.
- Do not port a generator before the conformance kit exists in
  jspassgen. A ported generator must make the same draws in the same
  order as the core and pass every vector of the kit.
- Never invent a feature, a benchmark number or a registry
  availability: nothing is published until the maintainer publishes it.

## The gate

- `make check`: versions, whitespace format (library within 100
  columns), the 500-line file limit, `mvn -B verify` (javac
  `-Xlint:all -Werror`, unit tests, JaCoCo at 100 % line and branch
  coverage, PMD ceilings: cyclomatic 10, cognitive 15, 60 statements per
  method) and the Javadoc with doclint. "It compiles" is not done; see
  [DEVELOPMENT.md](DEVELOPMENT.md).
- Every behaviour change lands with its test in the same commit.
- A dependency or plugin change needs `make lock` and an explanation in
  the commit body.

## Commits

- Conventional commits, imperative subject of at most 50 characters,
  body wrapped at 72 explaining what and why.
- Every commit is signed off (`git commit -s`) by the maintainer only,
  and SSH-signed. Agents record their part with an `Assisted-by:`
  trailer. Never rewrite pushed history, never force-push, never publish
  to Maven Central or merge into `main` without the maintainer.

## Leave alone

- `LICENSE-APACHE`, `LICENSE-MIT`, `LICENSES/`, `DCO.txt` and `KEYS.asc`,
  unless the task is about them.
- Generated output (`target/`).
- `.mvn/checksums/` changes only through `make lock`.
