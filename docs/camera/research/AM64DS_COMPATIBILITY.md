# AM64DS compatibility

## Existing functionality to preserve

AM64DS already supplies:

- movement analog values through fake Slot-2 hardware;
- European runtime patch support;
- D-pad camera mapping;
- touch-camera UI cleanup intent.

Canonical European payload SHA-256:

```text
e68025c3aad3a47941ab2903dd9d212b91bafedff705ea6252677c27d07bdb1c
```

## New relationship

```text
AM64DS mode 0/1
└── left-stick movement

ThorDS mode 2
└── right-stick camera state
```

Smooth Orbit must not change movement registers or movement feel.

## Profile composition

Recommended effective order:

1. exact ROM identity;
2. canonical AM64DS movement runtime code;
3. smooth-camera runtime code;
4. True Widescreen runtime code, when requested;
5. user cheats.

Conflicts and expected-word guards must be explicit.

## Provenance

Do not relabel the AM64DS movement patch as ThorDS-authored. The new camera protocol and camera patch are ThorDS-authored and require separate provenance and hashes.
