# Termen en naamgeving

## Product

- **ThorDS Enhanced**: onze fork en gebruikerservaring.
- **MelonDualDS**: de gekozen Android-basisfork.
- **melonDS core**: de native DS/DSi-emulatiekern.
- **AYN Thor**: de fysieke Android dual-screenhandheld.

## Profielen

- **Original**: geen gamepatches, geen overclock, native aspect; geschikt als basis voor Hardcore.
- **Enhanced**: één of meer gecureerde patches/emulatorfeatures actief.
- **Enhancement Profile**: declaratieve gamevariant plus patches, controller, display, renderer en policy.
- **User cheat**: door de gebruiker toegevoegde cheat; niet hetzelfde als gecureerde enhancement.
- **Runtime patch**: Action Replay-code die tijdens emulatie actief is en het ROM-bestand niet wijzigt.
- **Delta patch**: IPS/BPS-patch die op een app-private cachekopie wordt toegepast.

## Widescreen

- **Anamorphic stretch**: heel 4:3-frame naar 16:9 uitrekken. Dit vervormt 2D.
- **Game aspect patch**: wijzigt de perspectief-/clipperaspectratio zodat de 3D-wereld voor breedbeeld wordt gerenderd.
- **True Widescreen**: game aspect patch plus afzonderlijke presentatie van 3D en 2D, zonder uitgerekte HUD.
- **4:3 safe area**: gecentreerd gebied waar 2D in oorspronkelijke verhouding wordt getekend.
- **2D-only scene**: scherm zonder betrouwbare 3D-wereld, bijvoorbeeld menu of title.
- **Ambiguous scene**: compositormodus waarbij laagrol niet veilig kan worden bepaald; valt terug naar 4:3.

## RetroAchievements

- **RA hash**: systeemafhankelijke game-identiteit, niet noodzakelijk de MD5 van het volledige bestand.
- **Casual**: achievements actief zonder Hardcorebeperkingen.
- **Hardcore**: competitieve modus met integriteitsregels.
- **User-Agent**: unieke clientidentiteit in RA-verzoeken.
- **Rich Presence**: actuele gamestatus.
- **Leaderboard**: RA-score-/tijdinzending, volgens de bestaande modepolicy.

## Timing

- **Present FPS**: frames die Android/display presenteert.
- **Emulated FPS**: DS-videoframes per seconde.
- **Unique gameplay FPS**: unieke gamelogica-/renderupdates; essentieel voor 60fps-beoordeling.
- **Fast-forward**: volledige emulatie sneller dan wandklok; niet hetzelfde als overclock.
- **ARM9 overclock**: extra ARM9-uitvoercapaciteit binnen normale DS-wandklok.
- **Scheduler debt**: achterstand tussen gewenste en uitgevoerde emulatiecycli.
