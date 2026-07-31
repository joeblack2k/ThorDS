# ARM9-overclockarchitectuur

## Status in v0.1

`Experimental foundation`. Geen standaardwaarde boven 100%.

## Capabilitymodel

```text
Arm9OverclockCapability
├── UNSUPPORTED
├── PLUMBING_ONLY
├── EXPERIMENTAL
└── VALIDATED
```

Profile UI toont alleen waarden >100 wanneer capability minimaal `EXPERIMENTAL` is; release defaults gebruiken pas `VALIDATED`.

## Configlagen

```text
global developer default
profile requested ratio
session policy
Hardcore override
effective ratio
```

Hardcore forceert 100%.

## Datamodel

```text
Arm9OverclockConfig {
    enabled: Boolean
    percent: Int
}
```

Native voorkeur:

```text
ratioNumerator
ratioDenominator
fractionalDebt
```

## Core API

Voeg een expliciete API toe, bijvoorbeeld:

```cpp
void SetArm9OverclockRatio(u32 numerator, u32 denominator);
Arm9OverclockTelemetry GetArm9OverclockTelemetry() const;
```

Geen directe publieke fieldwrites vanuit Androidbridge.

## Timingstrategie

Luna onderzoekt core scheduler en schrijft ADR. Vereisten voor implementatie:

- extra ARM9 work;
- scheduler events op normale DS-time;
- ARM7 normale verhouding;
- GPU/SPU/VBlank normale tijd;
- DMA en IPC correct;
- JIT/interpreter gelijkwaardige semantics;
- deterministic reset;
- integer rounding met debt.

## Runtimeverandering

V0.1:

```text
change setting
→ mark relaunch required
→ flush save
→ stop session
→ recreate NDS instance with ratio
```

Geen live wijzigen totdat bewezen.

## Save state

Opties:

1. ratio opslaan in nieuwe frontendmetadata en load alleen bij gelijk;
2. ratio als core state serialiseren.

V0.1 mag frontendmetadata gebruiken zolang mismatch hard wordt geblokkeerd en normale SRAM-save compatibel blijft.

## Telemetry bridge

Native event/sample:

```text
effective percent
arm9 instructions/cycles
arm9 target delta
arm9 debt
arm7 delta
gx stall count
frame deadline miss
audio underrun
```

Android debug/performance export schrijft CSV/JSON onder evidence.

## Failure guards

- invalid percent → 100;
- overflow → 100;
- unsupported core → 100;
- Hardcore → 100;
- state mismatch → load reject;
- thermal severe → waarschuw, maar verander emulatie niet stilzwijgend;
- scheduler anomaly → auto-disable next launch and preserve report.

## 60fpsrelatie

OC maakt een 60fps-gamepatch niet automatisch correct. Hij levert slechts CPU-headroom. Game-side skip/delta/physicspatch blijft afzonderlijk.

## Rollback

Houd corechange klein en achter compile/runtimeflag. Een build met `THORDS_ARM9_OC=0` moet equivalent aan upstream zijn.
