# Core fork policy

The melonDS core is a Git submodule. A local-only core commit cannot be referenced by a public superproject.

## Required workflow when core code changes

1. inspect the exact current submodule commit and remotes;
2. create or reuse one public fork:

```text
https://github.com/joeblack2k/melonDS-android-lib
```

3. preserve the original core repository as `upstream` inside the submodule;
4. create bounded core commits with tests;
5. push the core commit to the public fork;
6. verify it with `git ls-remote`;
7. update the superproject gitlink;
8. update `.gitmodules` only when necessary so a clean recursive clone can fetch the forked commit;
9. record original base commit and ThorDS core commits in `docs/project/SOURCE_LOCK.md`;
10. build from a clean recursive checkout before final acceptance.

## Prohibited

- dangling local submodule commits;
- copying the entire core into the Android tree to avoid Git;
- force-pushing the core fork;
- changing unrelated core code during the OC workstream;
- losing upstream provenance or GPL notices.
