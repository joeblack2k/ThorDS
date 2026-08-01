# Evidence and privacy

## Public

Allowed:

- SHAs;
- addresses;
- minimal original-word guards;
- generated ThorDS code;
- aggregate timing JSON;
- test matrices;
- redacted logs.

## Private

Never commit:

- ROM/path/filename;
- patched ROM/xdelta without approval;
- saves/states;
- screenshots/video;
- serial number;
- account data.

## Before push

Run staged and history scans. Treat public evidence as hostile-input review:
inspect every path and line.
