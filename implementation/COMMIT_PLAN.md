# Commitplan

Gebruik gerichte commits. Onderstaande onderwerpen zijn richtlijn; splits wanneer een diff te groot wordt.

1. `docs: add ThorDS Enhanced project specification`
2. `build: pin and verify MelonDualDS rc5 baseline`
3. `test: add Thor display and session diagnostics`
4. `app: rebrand fork and isolate update channel`
5. `profile: add enhancement profile domain model`
6. `profile: add exact ROM identity resolver`
7. `patch: separate curated runtime codes from user cheats`
8. `profile: add SM64DS Europe definitions`
9. `input: add radial Slot-2 analog processing`
10. `input: add SM64DS right-stick camera overlay`
11. `render: add widescreen presentation configuration`
12. `render: add structured true-widescreen compositor`
13. `render: add scene classifier and safe fallback`
14. `profile: enable validated SM64DS true widescreen`
15. `ra: gate hardcore by resolved session integrity`
16. `ra: identify ThorDS Enhanced with unique user agent`
17. `core: add ARM9 overclock configuration plumbing`
18. `core: add ARM9 timing telemetry`
19. `core: add experimental validated overclock strategy` — alleen indien bewezen
20. `ui: add Thor-first enhancement controls`
21. `ui: add lower-screen pause experience`
22. `test: add ThorDS release regression harness`
23. `release: complete v0.1 evidence and notices`

## Commitregels

- Iedere commit bouwt of heeft expliciet testscope.
- Generated SPIR-V hoort bij de shadercommit.
- Submodulewijziging is aparte commit.
- Geen ROM/evidence-private.
- Geen mixing van formatting en feature.
- Commitmessage noemt geen ongeverifieerde claim zoals `true widescreen complete` vóór gate.
