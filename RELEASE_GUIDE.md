# Scrolls Logging Library Release Guide

Disclaimer: This release guide assumes you are an employee of Mobi Lab and have access to the company's account in Nevercode CI. The general guide for manually publishing is described in `PUBLISHING.md`.

Note: Every time you notice something in this guide is out-of-date or incorrect then fix it right away. Only then does this document have any value.

## Prerequisites

1) Make sure all the new features have been committed to `dev` branch.

2) Make sure all the functionality in the `dev` branch works.

3) Make sure the code in the `dev` builds correctly with the release build task:

```
./gradlew buildAllRelease
```

## Release process

1) Update the `CHANGELOG.md` document on the `dev` branch, add a section for this new release. Commit and push the change:

```bash
git add CHANGELOG.md
git commit -m "Added changelog for version X.Y.Z"
git push
```

2) Make sure the version code in the `dev` branch is correct. If needed then update the Scrolls library version from the `build.gradle` file in the project root folder:

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

2) Make sure the `Dev build (dev branch)` at Nevercode CI is build ok and everything is in green.

3) Merge the `dev` branch to `master`.

```bash
git checkout dev
git pull
git checkout master
git pull
git merge dev
git push
```

4) Start the `Release build (master branch)` at Nevercode CI, make sure it built fine and everything is in green.

5) Download the artifacts from the `Release build (master branch)` and make sure everything is in order:

- Sample app works as expected
- Library artifact is present and has the correct version

6) Start the `Publish build (master branch)` at Nevercode CI, make sure it built fine and everything is in green and the new version is published to JCenter.

7) Download the new version to some application and make sure it works as expected.

8) Create a new release tag in GitHub and link it from Bintray version page

- Open up the GitHub release page at https://github.com/MobiSolutions/scrolls-android/releases, create a new release `vX.Y.Z`. 
- Open up the Bintray version page at https://bintray.com/mobilab/mobi.lab.scrolls/scrolls-android/X.Y.X, press `Edit`, pick the correct value (`vX.Y.Z`) from `Choose GitHub tag` , press `Update`.

## Post-release actions

1) In the `dev` branch update the library version code to a new version so the dev branch code and the released code does not have a matching version.

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

