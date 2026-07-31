# M3 publication safety gate

Date: 2026-07-31

Canonical ThorDS source:

```text
https://github.com/joeblack2k/ThorDS
```

GitHub authentication was verified as `joeblack2k`; the repository was created
public without starter files. `origin` points to that repository and `upstream`
remains `https://github.com/SapphireRhodonite/melonDS-android.git`.

The pushable history and M3 worktree were checked for:

- ROM, SRL, save, and derived ROM file paths;
- credentials, tokens, keystores, private keys, and high-confidence secret
  patterns;
- private local paths and ADB device identifiers;
- image evidence and local Git author email addresses.

No ROM, save, high-confidence secret, private path, device identifier, or
image evidence is present in a pushable ref. The two local M2 display captures
and all local M3 UI captures were excluded from the public repository. The
retained M2/M3 evidence is textual and redacted.

The M3 acceptance commit is tagged `m3-product-identity` and published on
`main`.
