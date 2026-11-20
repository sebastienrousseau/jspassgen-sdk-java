<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Security Policy

## Supported versions

While pre-1.0, the latest released `0.0.x` receives security fixes;
older `0.0.x` versions do not.

| Version | Status | Receives security fixes? |
| :--- | :--- | :--- |
| 0.0.23 | Latest release | Yes |

## Reporting a vulnerability

**Do not open a public issue for security reports.**

Report privately through GitHub Private Vulnerability Reporting:
<https://github.com/sebastienrousseau/jspassgen-sdk-java/security/advisories/new>.
The maintainer is notified automatically.

**Acknowledgement**: within 48 hours. **Triage**: within 7 days.

A vulnerability in jspassgen's algorithms belongs to
[jspassgen](https://github.com/sebastienrousseau/jspassgen/security/advisories/new).

## Security posture

### Scope

A library that runs inside your JVM. It draws random values from
`SecureRandom` and, once generators are ported, turns them into
passwords and passphrases. It makes no network call, writes no files,
logs nothing and sends no telemetry.

### Threat model

| Surface | How it's handled |
| :--- | :--- |
| **Weak randomness** | Only `java.security.SecureRandom`, the platform CSPRNG; a caller may inject another `SecureRandom` instance, never a `java.util.Random`. |
| **Modulo bias** | `randomInt` rejects 32-bit draws at or above the largest multiple of `max`; a unit test detects a bare modulo. |
| **Secret exfiltration** | No network, file or logging code; no runtime dependency. |
| **Dependency CVEs** | No runtime dependency. Test and build dependencies are pinned by version and SHA-256 (`.mvn/checksums/`), audited with OSV-Scanner and updated by Dependabot. |
