# Scrolls Logging Library Release Guide

Disclaimer: This release guide assumes you are an employee of Mobi Lab and have access to the company's account in Codemagic CI.

Note: Every time you notice something in this guide is out-of-date or incorrect then fix it right away. Only then does this document have any value.

## Prerequisites

1) Access to Lab's accounts at Codemagic (https://codemagic.io/apps) and OSSRH https://s01.oss.sonatype.org/

2) Make sure all the new features have been committed to `develop` branch.

2) Make sure all the functionality in the `develop` branch works.

3) Make sure the code in the `develop` builds correctly with the release build task:

```
./gradlew buildAllRelease
```

## Release process

1) Update the `CHANGELOG.md` document on the `develop` branch, add a section for this new release. If possible then follow https://keepachangelog.com/en/1.0.0/

Commit and push the change:

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
git commit -m "Update the version code to X.Y.Z"
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

4) Start the release build (release-builds) at Codemagic CI from the `master` branch, make sure it built fine and everything is in green.

5) Start the publish build (publish-builds) at Codemagic CI from the `master` branch, make sure it built fine and everything is in green.

6) Open up  https://s01.oss.sonatype.org/, navigate to the Staging repositories, check the published repository there. 

6.1) Should be the same version number

6.2) Download the `scrolls-2.0.8-release.aar` artifact from there, make sure it is ok

7) Publish the staging repository at  https://s01.oss.sonatype.org/ 

7.1) First mark it as `Closed`. This button triggers a validation process for your project. If the validation passes then proceed, fix the issues otherwise (if you need to remove the repo and start again then use `Drop`)

7.2) Release the closed repository. Now the new artifact should be available shortly.

8) Create a new release tag in GitHub.

8.1) Open up the GitHub release page at https://github.com/MobiSolutions/scrolls-android/releases, create a new release `vX.Y.Z`. Don't write an additional changelog there, just link to the changelog document.

9) Do

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

## EXTRA: Manual publishing