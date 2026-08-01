# ROM safety

Before every commit/push:

```bash
git ls-files | rg -i '\.(nds|srl|rom|sav|dsv)$' && exit 1 || true
git log --all --name-only --pretty=format: \
  | rg -i '\.(nds|srl|rom|sav|dsv)$' && exit 1 || true
git diff --cached --name-only \
  | rg -i '\.(nds|srl|rom|sav|dsv|xdelta|bps|ips)$' && exit 1 || true
```

Private working directories must be ignored.

Rehash the source EU ROM after the entire run and prove it is unchanged.
