# Vooraf ingevulde beslislog

Kopieer relevante items naar afzonderlijke ADR’s onder `docs/project/adr/`.

## ADR-seed 001 — MelonDualDS rc5 als basis

**Status:** accepted  
**Besluit:** gebruik `SapphireRhodonite/melonDS-android` tag `0.7.0.rc5`.  
**Reden:** huidige Thor-, Vulkan-, analog- en RA-fundering.  
**Gevolg:** GPL-verplichtingen en upstreamsync blijven onderdeel van releasewerk.

## ADR-seed 002 — Runtimepatches vóór ROMpatching

**Status:** accepted  
**Besluit:** gebruik Action Replay voor analog en waar mogelijk widescreen.  
**Reden:** bron-ROM blijft identiek en RA kan de originele hash herkennen.  
**Gevolg:** Hardcore wordt bij actieve codes geblokkeerd.

## ADR-seed 003 — True Widescreen via Vulkan-layer separation

**Status:** accepted as target architecture  
**Besluit:** 3D naar 16:9, 2D-HUD naar 4:3-safe-area, 2D-only fallback.  
**Gevolg:** OpenGL is niet automatisch feature-equivalent.

## ADR-seed 004 — Casual blijft gebruikerskeuze

**Status:** accepted  
**Besluit:** enhancements blokkeren RA Casual niet.  
**Gevolg:** UI moet actieve enhancements transparant tonen.

## ADR-seed 005 — Overclockfundering en verplichte 60fps-productmijlpaal

**Status:** accepted  
**Besluit:** OC-plumbing en telemetry zijn fundering; 60fps is een verplichte
productgate en mag niet als voltooid worden gemarkeerd zonder volledige
timing-, gameplay-, audio- en stabiliteitsacceptatie.
**Gevolg:** v0.1 mag stabiel op originele gameplayframerate releasen.

## Nog te schrijven ADR’s

- effectieve ARM9-overclockstrategie;
- exact True Widescreen shader-/compositorpad;
- top/bottom displayrolalgoritme;
- profielcatalogusformaat;
- save-identiteit bij cachepatches;
- User-Agent en package-id;
- upstream core-submodulewijziging, indien nodig.
