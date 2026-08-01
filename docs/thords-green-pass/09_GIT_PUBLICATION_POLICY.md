# Git and publication policy

## Remotes

```text
origin   joeblack2k/ThorDS
upstream SapphireRhodonite/melonDS-android
```

Preserve both.

## Commit behavior

- one bounded commit per coherent green slice;
- tests and evidence may be separate bounded commits when useful;
- commit messages describe delivered behavior, not effort;
- push after each bounded green commit;
- no force-push or history rewrite;
- no generated build products unless the repository already intentionally tracks them.

## Pre-push gate

```bash
git diff --check
git status --short --branch
git ls-files | grep -Ei '\.(nds|srl|rom|sav|dsv)$' || true
git log --all --name-only --pretty=format: | grep -Ei '\.(nds|srl|rom|sav|dsv)$' || true
git ls-files | grep -E 'docs/evidence/private|credentials|tokens|keystore' || true
```

Run the repository's high-confidence secret scan. Known public cryptographic test vectors must be documented as existing false positives, not deleted blindly.

## Core submodule

Any new core commit must exist in a public reachable repository before the superproject gitlink is pushed. Follow `contracts/CORE_FORK_POLICY.md`.

## Final repository state

```text
origin/main contains all green commits
upstream remains unchanged
worktree clean
submodule commit reachable
no local-only branch required to build
```
