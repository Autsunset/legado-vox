# Legado Vox

**Legado Vox** is an independently maintained Android reading and in-app AI audiobook project.

It is derived from [Legado](https://github.com/gedoor/legado), a community Material Design 3 implementation, and the MiMo AI TTS integration from [VoxEngine](https://github.com/Autsunset/VoxEngine). This repository has its own package ID, version line, signing identity, source history, and release channel.

## Project identity

- App name: **Legado Vox**
- Release application ID: `io.github.autsunset.legadovox`
- Debug application ID: `io.github.autsunset.legadovox.debug`
- Repository: `https://github.com/Autsunset/legado-vox`
- Independent version line: starts at `1.0.0`

The internal source namespace remains `io.legado.app` to avoid unnecessary compatibility risk; the Android installation identity is controlled by the new application ID.

## In-app AI audiobook

MiMo AI TTS runs through Legado's own read-aloud pipeline, including reading progress, chapter navigation, the Material Design 3 player, caching, pre-synthesis, multi-character voice routing, and pitch-preserving speed adjustment.

It does **not** register an Android `TextToSpeechService` and does not install or change the system TTS engine.

## Build

JDK 21 and an Android SDK are required:

```bash
./gradlew verifyConfigArchitecture testAppDebugUnitTest assembleAppDebug
./gradlew assembleAppRelease
```

## Release signing

Release credentials are read from the ignored `signing.properties` file, Gradle properties, or environment variables. The keystore itself must never be committed. See the Chinese README for the property names and backup requirements.

## Privacy

The app is not connected to the upstream Firebase project and does not bundle Firebase Analytics or Performance configuration. Text and credentials are sent only when the user explicitly configures third-party network features such as AI TTS or synchronization.

## Upstream and license

A new Git history does not remove upstream copyright or license obligations. Thanks to [gedoor/legado](https://github.com/gedoor/legado), [HapeLee/legado-with-MD3](https://github.com/HapeLee/legado-with-MD3), [Autsunset/VoxEngine](https://github.com/Autsunset/VoxEngine), and all other upstream contributors.

This project remains licensed under [GPL-3.0](LICENSE).
