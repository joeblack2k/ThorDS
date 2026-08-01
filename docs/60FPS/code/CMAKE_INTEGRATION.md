# CMake integration

If new core files are added:

```cmake
target_sources(melonDS PRIVATE
    src/Sm64dsSemanticMonitor.cpp
)
```

Prefer header-only monitor logic if it avoids build-list duplication and keeps
the change bounded.

For superproject tests/tools:

- Python tools need no Gradle packaging.
- ARM patch sources remain host tooling, not APK assets unless the generated
  curated code is intentionally embedded.
- Do not package a ROM-derived binary.
