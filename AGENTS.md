# AGENTS.md

## General

1. Avoid superlatives and praise. Give direct, factual feedback.
2. When proposing or making changes to existing files, always read their current state first rather than assuming content from earlier discussion.
3. Manage all plugin, SDK, and library dependency versions in `gradle/libs.versions.toml`.
4. Keep user-facing UI strings in `scrolls-lib/src/main/res/values/strings.xml` so they remain localizable.
5. When addressing code review feedback where high-level advice conflicts with existing library behavior or introduces unnecessary complexity, discuss trade-offs with the developer before changing code.

## Shared Library Compatibility Rules (Critical)

`scrolls-android` (`mobi.lab.scrolls:scrolls`) is a **shared Android library published to Maven Central**, not a standalone application. Consuming apps upgrade build toolchains at their own pace.

1. **Separate build toolchain from consumer baseline:**
   - The repository itself may build with a modern Gradle wrapper, Android Gradle Plugin, and Gradle daemon JDK (`gradle/gradle-daemon-jvm.properties`), because AGP emits `minAndroidGradlePluginVersion=1.0.0` for `scrolls-lib` itself.
   - However, **everything shipped inside the published AAR or POM** (`compileOptions`, `minSdk`, `aarMetadata.minCompileSdk`, and runtime dependencies) must target the **lowest reasonable baseline** so consuming apps are never forced into major JDK, AGP, Kotlin, or `compileSdk` upgrades.
2. **Never copy app-level versions blindly:**
   - Do **not** copy `compileSdk`, `javaCompatibility`, or latest `androidx.*` versions directly from application projects into `scrolls-android`.
   - Keep `javaCompatibility` conservative (currently `VERSION_17`) so consumers compiling with JDK 17 / AGP 8.x can consume the published bytecode.
   - Keep `defaultConfig.aarMetadata.minCompileSdk` in `scrolls-lib/build.gradle` aligned with `minSdk` (`25`) unless Scrolls' own public APIs, manifest, or resources explicitly require newer Android platform attributes/types.
3. **Audit transitive dependencies before upgrading:**
   - Do **not** blindly apply `./gradlew dependencyUpdates` upgrades to `scrolls-lib` dependencies (`androidx.core`, `androidx.annotation`, etc.).
   - Before bumping any `implementation` or `api` dependency in `scrolls-lib`, check the candidate version's:
     - `aar-metadata.properties` (`minCompileSdk` and `minAndroidGradlePluginVersion`)
     - Java bytecode version and Kotlin metadata version (e.g. avoid pulling Kotlin 2.1+ metadata if consumers may still use older Kotlin plugins)
4. **Manifest & public API stability:**
   - Keep default `<activity>` declarations in `scrolls-lib/src/main/AndroidManifest.xml` with `android:exported="false"` and **without** hardcoded `android:theme` attributes so consuming apps inherit their application theme or merge custom activity overrides cleanly without `tools:replace="android:theme"`.
   - Preserve backwards compatibility on public classes and builders (`Log`, `LogImplFile`, `LogPost`, `LogPostBuilder`, `LogViewBuilder`, `FileCompressor`, `LogHelper`).

## Mandatory Verification After Build or Dependency Changes

Whenever modifying `build.gradle`, `gradle/libs.versions.toml`, SDK levels, or dependencies:

1. Run a clean release build and unit tests:
   ```shell
   ./gradlew clean buildAllRelease testDebugUnitTest
   ```
2. Inspect `scrolls-lib/build/intermediates/aar_metadata/release/writeReleaseAarMetadata/aar-metadata.properties` and verify `minCompileSdk` and `minAndroidGradlePluginVersion` match the intended consumer baseline.
3. Inspect resolved runtime dependencies via `./gradlew :scrolls-lib:dependencies --configuration releaseRuntimeClasspath` to ensure no transitive dependency raises `minCompileSdk` or `minAndroidGradlePluginVersion` unexpectedly.
4. Keep `README.md` ("Compatibility" section) and `CHANGELOG.md` updated whenever consumer baseline requirements change.

## Git, Shell & File Encoding Notes

- **UTF-8 without BOM:** Always write files as UTF-8 without BOM. Prefer IDE file editing tools (`write_file`, `replace_file_content`) over PowerShell `Out-File` / `Set-Content`.
- **Avoid Git pager hangs:** Always pass `--no-pager` when running `git diff` or `git log` in non-interactive shells (e.g. `git --no-pager diff`).
- **Windows Gradle environment:** If `AndroidLocationsException` occurs because both `ANDROID_PREFS_ROOT` and `ANDROID_USER_HOME` point to the same folder, clear `ANDROID_PREFS_ROOT` for the session (`$env:ANDROID_PREFS_ROOT = $null`).
