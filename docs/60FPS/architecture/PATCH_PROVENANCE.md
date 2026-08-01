# Patch provenance

## ThorDS-authored artifacts

Commit:

- ARM assembly/source;
- generator;
- manifest;
- original-word guard values;
- generated Action Replay words;
- hashes;
- public test summaries.

## Never commit

- original/patched ROM;
- copied ROM code beyond minimal guard words;
- private xdelta unless license/permission allows;
- private save/state/capture.

## Community reference

The known video is attributed to gamemasterplc and targets USA Rev1.

The current repository must remove the unrelated N64 SM64Games page from the
SM64DS provenance chain.

## Reproducibility

A clean local user with the exact EU ROM must be able to:

1. verify identity;
2. run the patch generator;
3. reproduce the same curated-code SHA;
4. build ThorDS without a patched ROM.
