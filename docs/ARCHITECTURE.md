<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Architecture

`jspassgen-sdk-java` is a native Java port of jspassgen's core. This
page is the map for contributors.

## The shape

```text
caller ──► generators (not ported yet)
             │  every random value through one interface
             ▼
           RandomSource ──► SecureRandomSource ──► java.security.SecureRandom
```

The port mirrors `packages/core` in jspassgen, which separates the
generators from the source of randomness with `RandomGeneratorPort`.
`RandomSource` is that port: `bytes(n)` for `generateRandomBytes` and
`randomInt(max)` for `generateRandomInt`. Replaying a recorded sequence
of draws through a scripted `RandomSource` is how the conformance kit's
draw tapes will check that this port makes the same draws, in the same
order, as the core.

## Source layout

| Path | What it holds |
| :--- | :--- |
| `src/main/java/com/jspassgen/RandomSource.java` | The random port |
| `src/main/java/com/jspassgen/SecureRandomSource.java` | The `SecureRandom` implementation, unbiased `randomInt` |
| `src/main/java/com/jspassgen/Jspassgen.java` | `VERSION` |
| `src/test/java/com/jspassgen/` | Unit and statistical tests, `ScriptedRandom` |

## Where the guarantees are enforced

| Guarantee | Enforced by |
| :--- | :--- |
| Unbiased integers | `SecureRandomSourceTest` (rejection path, chi-squared, modulo-bias check) |
| 100 % line and branch coverage | JaCoCo `check` in `mvn verify` |
| Complexity ceilings | PMD (`config/pmd/complexity.xml`) in `mvn verify` |
| No warnings | `javac -Xlint:all -Werror`, Javadoc doclint |
| Pinned supply chain | `.mvn/checksums/`, the enforcer plugin, actions pinned by SHA |
| Versions agree everywhere | `scripts/verify-release-versions` |
