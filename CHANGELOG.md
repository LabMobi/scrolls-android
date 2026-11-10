# Changelog

## [2.1.0] - 2026-11-10
### Added
- Added "Scroll to top" and "Scroll to bottom" options menu actions in `LogReaderActivity`.
- Added fallback `Toolbar` in `GuiHelper` so viewer titles and options menus remain accessible when host apps use `NoActionBar` themes.

### Changed
- Updated Gradle to 9.8.2, Android Gradle Plugin to 9.5.1, Java compatibility to 21, and migrated dependencies to Gradle Version Catalog (`compileSdk 37`, `targetSdk 36`, `minSdk 25`).
- Replaced deprecated `androidx.legacy:legacy-support-v4` with `androidx.core:core` and updated `androidx.annotation:annotation`.
- Enabled `.zip` log file compression by default when posting/sharing logs (`compressLogFile = true` in `LogPost`, `LogPostBuilder`, `LogViewBuilder`, and viewer activities; pass `setCompressLogFile(false)` to share raw `.txt` files).

### Removed
- Removed legacy log post tags functionality (`addTags`, `setTags`, `EXTRA_POST_TAGS`, and the tag input field in the post confirmation dialog).

### Fixed
- Fixed edge-to-edge `WindowInsets` handling in `LogListActivity`, `LogReaderActivity`, and `LogPostActivity` on Android 15+ (SDK 35+).
- Fixed `LogReaderActivity` not forwarding the `compressLogFile` setting when posting logs, and updated `LogPostImpl` to use `application/octet-stream` for raw `.txt` attachments and explicit URI read grants (`ClipData` + `FLAG_GRANT_READ_URI_PERMISSION`) on `ACTION_SEND`.

## [2.0.9] - 2021-06-28
### Changed
- Moved to AndroidX

## [2.0.8] - 2021-04-08
### Changed
- Published to Maven Central

## [2.0.7] - 2019-09-11
### Added
- Made the file compression logic usable separately via the FileCompressor class.

### Fixed
- Bug fixes - LogViewBuilder now also supports the log file compression parameter.

## [2.0.6] - 2019-09-10
### Added
- Added an option to compress a log to zip file before posting it.

### Fixed
- Cleaned up some Javadoc

## [2.0.5] - 2019-09-06
### Added
- Added a guide for publishing
- Published the artifact to JCenter

## [2.0.4] - 2019-09-04
### Changed
- Moved the library to GitHub
- Added the binary artifact to JCenter.
- Updated the build script.
- APIs are unchanged.