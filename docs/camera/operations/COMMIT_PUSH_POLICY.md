# Commit and push policy

## Principles

- One bounded intent per commit.
- No force-push.
- Push the core commit before updating the submodule pointer.
- Verify every remote SHA.
- Keep `upstream` remotes.
- Never mix ROM/private cleanup with feature code.

## Suggested commits

```text
docs: add smooth camera implementation dossier
test: add deterministic SM64DS camera telemetry
input: replace digital right-stick camera with smooth state
core: expose Slot-2 smooth camera protocol
input: bridge smooth camera state into melonDS core
patch: add guarded SM64DS EU smooth orbit camera
ui: integrate smooth camera settings and camera HUD cleanup
test: validate smooth camera on AYN Thor
```

## Push verification

```bash
git push origin HEAD:main
git ls-remote --heads origin main
git status --short --branch
```
