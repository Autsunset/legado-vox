# Changelog

## 1.1.2

- Replace the default launcher icon with reading-themed artwork and retain the supplied portrait as an alternative.
- Unify read-aloud engine, voice, cache, casting, and editor layouts with the reading settings theme.
- Inherit reading-menu colors and fonts across read-aloud settings destinations.
- Keep settings in the navigation window and use short, symmetric transitions without page scaling.
- Preserve the selected settings tab and player return context when navigating between settings pages.

## 1.0.0

- Establish Legado Vox as an independent application with a new package ID and signing identity.
- Integrate MiMo AI TTS into Legado's in-app audiobook pipeline.
- Keep all audiobook and settings UI based on the Legado Material Design 3 implementation.
- Do not register or modify the Android system TTS engine.
- Remove the upstream Firebase project binding and start an independent release/version line.
