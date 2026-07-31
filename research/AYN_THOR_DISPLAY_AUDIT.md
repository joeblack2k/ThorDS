# AYN Thor displayaudit

## Bekende bronondersteuning

Rc5 bevat een specifieke mapper die de displaynamen:

```text
Built-in Screen
Screen-2
```

als ingebouwde AYN Thor-schermen herkent.

Dit bewijst apparaatintentie, niet de actuele firmwarelayout. ADB blijft de bron van waarheid.

## Verwachte fysieke vorm

Nominaal:

- boven: ongeveer 1920×1080, breed;
- onder: ongeveer 1240×1080, touchscreen.

Gebruik nooit alleen deze waarden om display-ID’s te kiezen. Firmware, Android window contexts, rotations en system insets kunnen andere effectieve afmetingen rapporteren.

## Verplichte inventaris

```bash
adb shell getprop ro.product.manufacturer
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
adb shell getprop ro.build.fingerprint
adb shell dumpsys display
adb shell dumpsys window displays
adb shell wm size
adb shell wm density
adb shell settings get system user_rotation
```

De app logt per display:

- ID;
- naam;
- flags;
- type;
- state;
- mode en supported modes;
- refresh rate;
- real metrics;
- window metrics;
- density;
- rotation;
- presentation eligibility;
- toegewezen Thorrol.

## Roltoewijzing

Gebruik een gewogen classifier:

### Top panel

- ingebouwde AYN-naam;
- landscape-aspect duidelijk groter dan 4:3;
- grootste fysieke breedte/pixeloppervlak;
- verwacht geen primair DS-touchdoel.

### Bottom panel

- ingebouwde AYN-naam;
- near-square/ongeveer 1.15 aspect;
- fysiek lagere resolutie;
- touchscreencontext.

### Fallback

Wanneer niet overtuigend:

- behoud upstream current/secondary behavior;
- toon developerdiagnostiek;
- laat gebruiker eenmaal rollen omwisselen;
- sla alleen een device-fingerprintgebonden override op;
- bied `Reset display mapping`.

## Layout

### Top

Voor werkelijke viewport `W×H`:

```text
True Widescreen world: W×H
4:3 UI-safe-width: min(W, H × 4/3)
safe-x: (W - safe-width) / 2
```

Bij 1920×1080:

```text
safe width = 1440
safe x = 240
```

### Bottom

Fit een 4:3-beeld binnen `W×H`.

Bij 1240×1080:

```text
fit width = 1240
fit height = 930
vertical bars = 75 + 75
```

## Lifecyclecases

Test:

- cold start open;
- app naar achtergrond;
- scherm dicht/open indien gerapporteerd;
- Android sleep;
- USB disconnect/reconnect zonder appcrash;
- Presentation `onStop`;
- display changed;
- app relaunch;
- activity gestart op onverwacht display;
- screen swap en recovery.

## Touch

Touch moet uitsluitend naar het getoonde bottom DS-rect worden gemapt:

```text
dsX = clamp((touchX - rect.left) / rect.width × 256, 0, 255)
dsY = clamp((touchY - rect.top) / rect.height × 192, 0, 191)
```

Touches in letterboxbars worden genegeerd of alleen door expliciete ThorDS UI afgehandeld.

## Bewijs

Maak een diagnostisch scherm dat op ieder fysiek paneel zijn rol, ID, naam, afmetingen en actuele DS-bron toont. Fotobewijs of afzonderlijke surfacecaptures moeten aantonen dat de labels fysiek kloppen.
