# ARM9-overclockacceptatietests

## O-00 100%-equivalence

Upstream versus ThorDS 100%:

- same save progression;
- same frame/update cadence;
- same audio duration;
- same deterministic trace where feasible;
- no performance regression beyond instrumentation budget.

## O-01 Config

- values 100/125/150/175/200;
- invalid clamps/rejects;
- persistence;
- profile override;
- Hardcore force 100.

## O-02 Wall clock

Run scripted/observed 10 minutes.

```text
game elapsed / wall elapsed difference <= 0.1%
```

Test each effective ratio.

## O-03 Audio

- pitch;
- sample rate;
- underruns;
- duration;
- no crackle.

## O-04 RTC/timers

- RTC seconds;
- game timers;
- menu timers;
- animation cadence.

## O-05 ARM7/IPC

Look for desync, hangs, audio command issues.

## O-06 GPU/GXFIFO

- stalls;
- missing geometry;
- FIFO overflow/underflow;
- renderer consistency.

## O-07 JIT/interpreter

At minimum 100% both. For >100%, semantics must match selected supported execution mode; unsupported mode clearly disabled.

## O-08 Save state

- save at ratio;
- load same ratio;
- mismatch rejected or safe conversion;
- SRAM unaffected.

## O-09 Runtime change

Attempt change active:

- UI requires relaunch;
- no half-applied ratio.

## O-10 Stress

SM64DS known 60fps-pressure scenes even at 30fps:

- Chain Chomp;
- mountain;
- King Bob-omb;
- effects;
- water;
- many actors.

Measure headroom.

## O-11 Generic DS smoke

At least several non-SM64DS homebrew/synthetic or user-available games; no broad timer break.

## Capability result

```text
PLUMBING_ONLY
EXPERIMENTAL_<ratios>
VALIDATED_<ratios>
```

No binary `supported` without ratio detail.
