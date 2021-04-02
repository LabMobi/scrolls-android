# Scrolls Logging Library Release Guide

Disclaimer: This release guide assumes you are an employee of Mobi Lab and have access to the company's account in Codemagic CI. The general guide for manually publishing is described in `PUBLISHING.md`.

Note: Every time you notice something in this guide is out-of-date or incorrect then fix it right away. Only then does this document have any value.

## Prerequisites

1) Make sure all the new features have been committed to `develop` branch.

2) Make sure all the functionality in the `develop` branch works.

3) Make sure the code in the `develop` builds correctly with the release build task:

```
./gradlew buildAllRelease
```

## Release process

1) Update the `CHANGELOG.md` document on the `develop` branch, add a section for this new release. Commit and push the change:

```bash
git add CHANGELOG.md
git commit -m "Added changelog for version X.Y.Z"
git push
```

2) Make sure the version code in the `develop` branch is correct. If needed then update the Scrolls library version from the `build.gradle` file in the project root folder:

```groovy
/**
 * Major version component.
 */
final String VERSION_MAJOR = "0" // Update HERE!
/**
 * Feature version component.
 */
final String VERSION_FEATURE = "0" // Update HERE!
/**
 * Tweak version component.
 */
final String VERSION_MINOR = "0" // Update HERE!
```

Commit and push the changes

```bash
git add build.gradle
git commit -m "Update the version code to X.Y:Z"
git push
```

2) Make sure the develop build you just started at Codemagic CI is ok and everything is in green.

3) Merge the `develop` branch to `master`.

```bash
git checkout develop
git pull
git checkout master
git pull
git merge develop
git push
```

4) Start the release build at Codemagic CI, make sure it built fine and everything is in green.

5) Download the artifacts from the release build you just built and make sure everything is in order:

- Sample app works as expected
- Library artifact is present and has the correct version

6) Start the public build at Codemagic CI, make sure it built fine and everything is in green and the new version is published to https://s01.oss.sonatype.org/ and is available from Maven Central.

7) Download the new version to some application and make sure it works as expected.

8) Create a new release tag in GitHub 

- Open up the GitHub release page at https://github.com/MobiSolutions/scrolls-android/releases, create a new release `vX.Y.Z`. 

## Post-release actions

1) In the `develop` branch update the library version code to a new version so the `develop` branch code and the released code does not have a matching version.

```clojure
/**
 * Major version component.
 */
final String VERSION_MAJOR = "0" // Update HERE!
/**
 * Feature version component.
 */
final String VERSION_FEATURE = "0" // Update HERE!
/**
 * Tweak version component.
 */
final String VERSION_MINOR = "0" // Update HERE!
```

