# Performancebudgets

De exacte Thorbaseline wordt gemeten in M1/M2. Onderstaande waarden zijn startgates, geen vervanging voor empirische vergelijking.

## Frame pacing

Bij originele SM64DS-gameplay:

- emulatie realtime;
- geen audio underruns;
- present P95 binnen displaybudget;
- geen herhaaldelijke deadline misses;
- geen zichtbare stutter door per-frame allocations.

## True Widescreen overhead

Ten opzichte van dezelfde Vulkan-internal-resolution in 4:3:

```text
GPU frame P50 overhead ≤ 10%
GPU frame P95 overhead ≤ 15%
CPU frontend overhead  ≤ 5%
memory steady overhead ≤ 64 MiB
```

Als layer-aware composition al in dezelfde pass past, mik lager.

## Launcher

- first content zichtbaar < 2 s na database gereed;
- scroll 60Hz zonder jank;
- ROMhashing op IO thread;
- geen UI freeze > 100 ms;
- profile resolve < 50 ms na cached identity.

## Input

- MotionEvent naar Slot-2-state binnen hetzelfde frontendframe;
- geen extra queue met onbeperkte backlog;
- camera digitalization zonder >1 frame kunstmatige vertraging.

## RA

- per-frame runtime overhead vergelijken met upstream;
- profile UI mag RA-call niet blokkeren;
- offline queue geen gameframe blokkeren.

## ARM9-OC

Meet:

- host CPU use;
- battery/thermal trend;
- frame deadline;
- audio;
- effective headroom.

Een hogere OC die de host laat throttlen en netto slechter presteert, wordt niet aanbevolen.

## Lange duur

60 minuten:

- geen lineaire heapgroei;
- geen groeiende Vulkan resources;
- geen Presentation leak;
- geen steeds langere frame times;
- save flush stabiel.

## Bewijsformaten

```text
docs/evidence/performance/baseline.json
docs/evidence/performance/widescreen.json
docs/evidence/performance/arm9-oc.csv
docs/evidence/performance/long-run.md
```
