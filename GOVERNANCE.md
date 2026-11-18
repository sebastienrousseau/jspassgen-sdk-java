<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Governance

How `jspassgen-sdk-java` is run, who decides, and what happens if its
maintainer is unavailable.

## Scope

A native Java port of jspassgen's core. What a generator produces is
decided in [`jspassgen`](https://github.com/sebastienrousseau/jspassgen)
and proven by its conformance kit; this repository decides only how
Java callers reach it.

## Roles and responsibilities

| Role | Who | Responsible for |
| :--- | :--- | :--- |
| **Maintainer** | Sebastien Rousseau ([@sebastienrousseau](https://github.com/sebastienrousseau)), the only maintainer | Merging pull requests, cutting and signing releases, Maven Central publication, security response, keeping the port in lockstep with the suite |
| **Contributor** | Anyone with a merged pull request | Proposing changes with tests, reviewing others' changes |
| **User** | Everyone | Reporting bugs, asking questions, requesting features ([SUPPORT.md](SUPPORT.md)) |

## Decisions

- **Day-to-day changes** land on the release branch
  `feat/v<next-version>` and reach `main` through its one pull request,
  once CI is green.
- **Significant changes** (a breaking API change, a runtime dependency,
  a Java floor raise) start as an issue and are recorded as an ADR in
  `docs/adr/`.
- **The version** moves by `0.0.1`, in lockstep with the suite; the
  maintainer decides when a release opens.

CI is the definition of done ([DEVELOPMENT.md](DEVELOPMENT.md)).

## Continuity plan

Items marked **Not yet arranged** have no continuity arrangement today.

| Asset | Today | Continuity |
| :--- | :--- | :--- |
| Source code | Apache-2.0 OR MIT, public history | Anyone can fork and continue it. |
| GitHub repository | Owned by the maintainer's account | **Not yet arranged:** no second owner. |
| Maven Central namespace `com.jspassgen` | Not yet verified (needs DNS verification of jspassgen.com) | **Not yet arranged.** |
| Release process | Not built yet; [DEVELOPMENT.md](DEVELOPMENT.md) states the planned model | **Not yet arranged.** |

## Conduct and security

Participation follows [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
Vulnerabilities go through [SECURITY.md](SECURITY.md), never a public
issue.
