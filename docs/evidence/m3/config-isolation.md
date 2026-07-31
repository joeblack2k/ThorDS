# M3 config isolation

The physical Thor reports all three independently installed packages:

```text
me.magnum.melondualds
me.magnum.melondualds.dev
io.github.joeblack2k.thords.dev
```

Android therefore assigns ThorDS its own application sandbox. In addition,
the ThorDS settings backup/mirror code reads and writes `ThorDS.opts`; it does
not read, create, delete, or replace the MelonDualDS `melonDualDS.opts` name.

The debug package persisted its Thor-specific soft-control default in its own
preference file:

```text
soft_input_behaviour=always_invisible
```
