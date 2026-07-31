# ThorDS Enhanced

Thor-first enhanced Nintendo DS emulator based on
[MelonDualDS 0.7.0.rc5](https://github.com/SapphireRhodonite/melonDS-android).
The canonical ThorDS source is
[github.com/joeblack2k/ThorDS](https://github.com/joeblack2k/ThorDS).

ThorDS does not bundle ROMs, saves, or Nintendo assets. Bring a legally obtained
ROM locally; it is never part of the source repository or release artifacts.

|Rom List|Dark Theme|Pocket Physics|Layout Editor|
|---|---|---|---|
|![Screenshot 1](./.github/images/screenshot_mobile0.png)|![Screenshot 2](./.github/images/screenshot_mobile1.png)|![Screenshot 3](./.github/images/screenshot_mobile2.png)|![Screenshot 4](./.github/images/screenshot_mobile3.png)|

# Missing Features
*  Local Multiplayer
*  DSi SD card support
*  Customizable button skins
*  More display filters

# Performance
Performance is solid on 64 bit devices with thread rendering and JIT enabled, and should run at full speed on flagship devices. Performance on older devices, specially
32 bit devices, is very poor due to the lack of JIT support.

# Integration with third-party frontends
It's possible to launch melonDS from third part frontends. For that, you will need to have the ROMs you want to launch already scanned by melonDS. Then, you can configure your
third-party frontend with the following configuration:
*  Package name: `io.github.joeblack2k.thords`
*  Activity name: `me.magnum.melonds.ui.emulator.EmulatorActivity`
*  Parameters (choose one):
    * Intent data (preferred) - a URI of the NDS ROM (ZIP and 7z files are supported). Ensure [read permission is granted](https://developer.android.com/reference/android/content/Intent#FLAG_GRANT_READ_URI_PERMISSION)
    * `uri` (deprecated) - a string with the [SAF](https://developer.android.com/guide/topics/providers/create-document-provider) URI of the NDS ROM (ZIP and 7z files are supported)
    * `PATH` (deprecated) - a string with the absolute path to the NDS ROM (ZIP and 7z files are supported)

### Pegasus metadata files
* [melonds.metadata.txt](./.github/pegasus/melonds.metadata.txt) 
* [melonds-nightly.metadata.txt](./.github/pegasus/melonds-nightly.metadata.txt) 

### Info regarding save files
When launching ROMs from third-party frontends, if melonDS hasn't scanned that particular ROM previously, it won't be able to create the save file next to the ROM file if the
option "Save next to ROM file" is enabled in the settings or the save file directory is not set. Instead, melonDS will create a save file in
`Android/data/io.github.joeblack2k.thords/files/saves`

# Releases

ThorDS v0.1 has no automatic updater. Do not install MelonDualDS binaries over
ThorDS; the package and signing lineage are intentionally separate.

# Building
To build the project you will need Android SDK, NDK and CMake.

## Build steps:
1.  Clone the project, including submodules with:
    
    `git clone --recurse-submodules https://github.com/joeblack2k/ThorDS.git`
2.  Install the Android SDK, NDK and CMake
3.  Build with:
    1.  Unix: `./gradlew :app:assembleGitHubProdDebug`
    2.  Windows: `gradlew.bat :app:assembleGitHubProdDebug`
4.  The generated APK can be found at `app/gitHubProd/debug`

If you want to create a release build, you will need to modify your `local.properties` file to include the following fields:  
*  `MELONDS_KEYSTORE=<path_to_your_keystore>`
*  `MELONDS_KEYSTORE_PASSWORD=<keystore_password>`
*  `MELONDS_KEY_ALIAS=<name_of_your_key_alias>`
*  `MELONDS_KEY_PASSWORD=<key_alias_password>`
