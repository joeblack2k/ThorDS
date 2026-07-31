# M3 Thor validation

The GitHub Prod debug APK was installed on the connected AYN Thor after the
publication-source changes.

Verified:

- `io.github.joeblack2k.thords.dev` coexists with the installed MelonDualDS
  packages;
- `soft_input_behaviour=always_invisible` is applied on Thor;
- `thords_safe_mode=false` is the restored default after the safe-mode probe;
- About displays the ThorDS source URL;
- tapping the ThorDS source entry dispatches `ACTION_VIEW` to
  `https://github.com/joeblack2k/ThorDS`;
- the offline notices view displays the same source URL and component index;
- filtered logcat contains no ThorDS FATAL EXCEPTION or ANR.

No screenshots, device serial, account data, or ROM content are included in
this public evidence.
