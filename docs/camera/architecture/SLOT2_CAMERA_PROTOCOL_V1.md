# Slot-2 smooth camera protocol v1

## Addressing

The existing `CartAnalog::ROMRead` derives a mode from bits `0xFF00`. Mode 0 and 1 are already used by AM64DS.

Smooth camera adds mode 2:

```text
NDS address: 0x09000200
masked cart address: 0x00000200
mode: 2
```

## Register table

| Offset | Type | Meaning |
|---:|---|---|
| `0x00` | `s16` | yaw input in Q12 |
| `0x02` | `s16` | pitch input in Q12; zero in v1 |
| `0x04` | `u16` | yaw units per game update |
| `0x06` | `u16` | monotonic recenter sequence |
| `0x08` | `u16` | magic `0x5343` |
| `0x0A` | `u16` | protocol version `1` |
| `0x0C` | `u16` | flags |
| `0x0E` | `u16` | reserved zero |

Flags:

```text
bit 0  enabled
bit 1  play original recenter sound
bit 2  pitch-capable; false in v1
```

## Q12 conversion

```text
-1.0 → -4096
 0.0 →     0
+1.0 → +4096
```

Clamp before conversion.

## Compatibility

Mode 0 and mode 1 reads must not change.

The protocol must return invalid/disabled values when:

- no `CartAnalog` is inserted;
- Smooth Orbit is not effective;
- Safe Mode or Original is active;
- protocol state has not been initialized.

## Savestate policy

Do not extend the existing `CartAnalog` savestate payload in v1.

Camera state is frontend-transient. Neutralize it before and after state loads. This avoids breaking older states and prevents a loaded state from inheriting a held right stick.
