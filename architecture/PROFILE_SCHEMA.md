# Profielschema

De uiteindelijke implementatie mag Kotlin serialization, JSON assets of een equivalent gebruiken. De semantiek staat vast.

## Voorbeeld

```json
{
  "schemaVersion": 1,
  "id": "sm64ds.eu.thor-enhanced",
  "profileVersion": 1,
  "displayName": "Super Mario 64 DS — Thor Enhanced",
  "game": {
    "system": "NDS",
    "gameCode": "ASMP",
    "revision": 0,
    "raGameId": 9983,
    "raHashes": [
      "ba3c4052e00c5cc31df5d5534c39de1b"
    ]
  },
  "device": {
    "preferredManufacturer": "AYN",
    "preferredModel": "AYN Thor",
    "allowGenericFallback": true
  },
  "enhancements": [
    {
      "id": "analog",
      "defaultEnabled": true,
      "kind": "ACTION_REPLAY",
      "requires": ["SLOT2_ANALOG"],
      "conflicts": ["RA_HARDCORE"]
    },
    {
      "id": "true_widescreen",
      "defaultEnabled": true,
      "kind": "COMPOSITE",
      "requires": [
        "VULKAN_STRUCTURED_COMPOSITOR",
        "SM64DS_EU_ASPECT_PATCH"
      ],
      "conflicts": ["RA_HARDCORE"]
    },
    {
      "id": "arm9_overclock",
      "defaultEnabled": false,
      "kind": "EMULATOR_FEATURE",
      "experimental": true,
      "conflicts": ["RA_HARDCORE"]
    },
    {
      "id": "fps60",
      "defaultEnabled": false,
      "kind": "COMPOSITE",
      "experimental": true,
      "hiddenUntil": ["FPS60_VALIDATED"]
    }
  ],
  "controller": {
    "slot2Analog": true,
    "leftStick": "SLOT2_XY",
    "rightStick": "DS_DPAD_CAMERA",
    "radialDeadzone": 0.10,
    "cameraPressThreshold": 0.55,
    "cameraReleaseThreshold": 0.35
  },
  "display": {
    "topDsScreenRole": "THOR_TOP_PANEL",
    "bottomDsScreenRole": "THOR_BOTTOM_PANEL",
    "topPresentation": "TRUE_WIDESCREEN_3D_UI_SAFE",
    "bottomPresentation": "FIT_4_3"
  },
  "retroachievements": {
    "allowOff": true,
    "allowCasual": true,
    "allowHardcore": false
  },
  "saveCompatibilityGroup": "sm64ds-eu-retail",
  "provenance": []
}
```

## Vereiste velden

### Identity

- schema/profile id en versie;
- system;
- gamecode;
- revision;
- minimaal één exacte systeemhash.

### Enhancements

Per enhancement:

- stabiele id;
- zichtbare naam/beschrijving;
- default;
- kind;
- dependencies;
- conflicts;
- relaunch requirement;
- experimental status;
- provenance reference;
- test status.

### Patch

Action Replay:

```text
codeWords
expectedOriginalWords/checks
codeSha256
```

Delta:

```text
format
sourceSha256
patchSha256
targetSha256
cachePolicy
```

### Capability

Voorbeelden:

```text
SLOT2_ANALOG
VULKAN
VULKAN_STRUCTURED_COMPOSITOR
THOR_DUAL_INTERNAL_DISPLAY
ARM9_OC_CORE_SUPPORT
RA_INTEGRATION
```

### Policy

- RA allowed modes;
- save-state/rewind;
- user cheats;
- performance flags;
- fallback behavior.

## Validatie

Catalogusbuild faalt bij:

- duplicate profile id;
- duplicate enhancement id;
- onbekende dependency;
- cyclic dependency;
- ontbrekende provenance;
- malformed AR words;
- patch zonder hash;
- Hardcore allowed terwijl profile-integrity dat tegenspreekt;
- default-enabled feature die capability niet kan fallbacken.

## Forward compatibility

Onbekende optionele velden mogen worden genegeerd; onbekende verplichte `kind` of hogere `schemaVersion` blokkeert het profiel veilig.
