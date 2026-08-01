# G0 — State reconciliation

## Goal

Start from the real current repository without destroying newer work.

## Steps

1. verify `origin`, `upstream`, HEAD and baseline ancestry;
2. inspect staged, unstaged and untracked files;
3. inspect current submodule commits and reachability;
4. read latest `STATUS.md` and `WORKLOG.md`;
5. confirm ADB Thor and physical displays;
6. confirm local ROM is ignored and its identity still matches without logging bytes;
7. compare current source against this dossier's baseline table;
8. update `03_CURRENT_BASELINE.md` only when the live repo is newer;
9. commit this dossier as one documentation commit after the safety scan if it is untracked.

## Exit

- no destructive action;
- current HEAD reconciled;
- all old partial gates preserved truthfully;
- next active workstream G1.
