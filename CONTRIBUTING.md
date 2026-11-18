<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Contributing

Thanks for looking. This repository is the native Java port of
jspassgen's core. The reference behaviour lives in
[`jspassgen`](https://github.com/sebastienrousseau/jspassgen); a change
to what a generator produces starts there.

## Before you open a pull request

Everything CI checks you can run locally with a JDK 17 or newer;
`./mvnw` fetches the pinned Maven and checks its SHA-256:

```sh
make check
```

It runs the version check, the whitespace and size gates, `mvn -B
verify` (unit tests, JaCoCo at 100 %, PMD) and the Javadoc with
doclint. `make docs-lint` runs the Markdown, spelling, REUSE and link
checks if you have the tools ([DEVELOPMENT.md](DEVELOPMENT.md)).

Maven refuses any dependency or plugin whose SHA-256 is not recorded in
`.mvn/checksums/`. After changing a version, including a Dependabot
bump, run `make lock` and commit the diff with the change.

## Porting a generator

Not yet: generators are ported only against jspassgen's conformance
kit, which does not exist yet. Once it does, a port vendors the kit
unchanged, records the jspassgen commit in `ci/core.ref`, and passes
every vector.

## Commits and pull requests

- Conventional commits (`feat:`, `fix:`, `docs:`, `test:`, `build:`,
  `ci:`, `chore:`), subject of at most 50 characters, body wrapped at 72
  saying what and why.
- Sign off every commit (`git commit -s`, see [DCO.txt](DCO.txt)).
- Pull requests go to the release branch `feat/v<next-version>`, never to
  `main` directly.
- Never commit real passwords, keys or tokens.
