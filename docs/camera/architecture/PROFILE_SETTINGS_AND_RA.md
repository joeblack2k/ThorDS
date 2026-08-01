# Profile, settings and RetroAchievements

## Profile identity

Only `sm64ds.eu.thor-enhanced` may request Smooth Orbit.

## Enhancement identity

Preferred migration:

- preserve the existing stable ID `right-stick-camera`;
- change the effective implementation from digital to smooth;
- change display text to `Smooth Orbit Camera`;
- increment the containing profile version;
- document the semantic migration.

This avoids orphaning existing preferences.

## Settings

Default Enhanced settings:

```text
Smooth Orbit Camera: On
Sensitivity: 100%
Maximum yaw speed: 165°/s
Deadzone: 12%
Response curve: 1.50
Invert horizontal: Off
R3 recenter sound: On
D-pad fallback: On
```

UI must display requested and effective state.

## RA policy

| Mode | Original | Enhanced |
|---|---:|---:|
| Off | allowed | allowed |
| Casual | allowed | allowed |
| Hardcore | allowed | blocked |

Hardcore selection while Enhanced is requested must offer:

- Original + relaunch in Hardcore;
- or remain Enhanced in Casual.

No silent downgrade and no hash spoof.
