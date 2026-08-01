# Core fork policy

- Verify GitHub authentication is `joeblack2k`.
- Preserve the core `upstream`.
- Branch from the exact current gitlink.
- Do not reuse failed research branches as product code without re-review.
- Push the core commit before updating the superproject.
- Verify `git ls-remote` contains the object.
- Test a clean recursive clone.
- No force-push.
