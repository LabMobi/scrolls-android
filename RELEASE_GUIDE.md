# Scrolls Logging Library Release Guide

Disclaimer: This release guide assumes you are an employee of Mobi Lab and have access to the company's account in Codemagic CI.

Note 1: Every time you notice something in this guide is out-of-date or incorrect—then fix it right away. Only then does this document have any value.

## Links

- Publishing info at the internal Confluence: https://labmobi.atlassian.net/wiki/spaces/DEV/pages/15990951/Sonatype+Maven+Access+Credentials
- Project repository on GitHub: https://github.com/MobiSolutions/scrolls-android
- Project Codemagic CI job: https://codemagic.io/app/6066d8b70be8c6080d67804c/settings
- Artifacts on Maven Central: https://repo1.maven.org/maven2/mobi/lab/scrolls/scrolls/ (can take some time)
  - Artifacts on Sonatype OSSRH: https://s01.oss.sonatype.org/content/groups/public/mobi/lab/scrolls/scrolls/ (available right away after publishing)
- Sonatype OSSRH UI: https://central.sonatype.com/publishing
  - NOTE: Login with the same account you publish with. Other accounts, even when connected to the same namespace, will not see uploaded staging repositories.
- Publishing plugin we use:
  - https://vanniktech.github.io/gradle-maven-publish-plugin/central/

## Prerequisites

1. Access to Lab's accounts at Codemagic (https://codemagic.io/apps) and OSSRH https://central.sonatype.com/publishing. For manual publishing—access the credentials at https://confluence.lab.mobi/display/DEV/Sonatype+Maven+Access+Credentials

2. Make sure all the new features have been committed to `develop` branch.

3. Make sure all the functionality in the `develop` branch works.

4. Make sure the code in the `develop` builds correctly with the release build task:

   ```
   ./gradlew buildAllRelease
   ```

5. Make sure the "Compatible versions" list in the `README.md` is up-to-date

## Release process

1. Update the `CHANGELOG.md` document on the `develop` branch, add a section for this new release. If possible—then follow https://keepachangelog.com/en/1.0.0/

Commit and push the change:

```
git add CHANGELOG.md
git commit -m "Add changelog for version X.Y.Z"
git push
```

1. Make sure the version code in the `develop` branch is correct. If needed—then update the library version from the `build.gradle` file in the project root folder:

```
ext {
    // Current version of the library
    libraryVersion = "X.Y.Z"
}
```

Commit and push the changes

```
git add build.gradle
git commit -m "Update the version code to X.Y.Z"
git push
```

1. Make sure the develop build (`develop-builds (Development builds)`) you just started at Codemagic CI is okay and everything is in green.
2. Merge the `develop` branch to `master`.

```
git checkout develop
git pull
git checkout master
git pull
git merge develop
git push
```

1. Start the release build (`release-builds (Release builds for verification (master branch only))`) at Codemagic CI from the `master` branch, make sure it builds fine and everything is in green.
2. Start the publish build (`publish-builds Publish to Maven builds (master branch only)`) at Codemagic CI from the `master` branch, make sure it builds fine and everything is in green.
3. Open up https://central.sonatype.com/publishing, navigate to "Deployments".
   - There should be one or more deployments waiting with the same version you are publishing. Pick the correct one, drop the others if there are more than one. This can happen if you run publishing multiple times.
   - Check if the artifacts are okay. If you want to cancel—then "Drop" the repository.
4. Publish repository by pressing "Publish"
   - Now the new artifact should be available shortly on the Maven Central at https://repo1.maven.org/maven2/mobi/lab/scrolls/scrolls/
   - NOTE: Depending in the time of day this can take some time (30m - 1h)
5. Create a new release and a Git tag in GitHub as follows:
   - Open up the GitHub release page at https://github.com/MobiSolutions/scrolls-android/releases, create a new release `vX.Y.Z`. Don't write an additional changelog there, just link to the changelog document.
   - Let it automatically create a tag for the release, in the form of "release-X.Y.Z".
6. OPTIONAL: Update the library in at least one of the projects using it to make sure everything is in order.

## Post-release actions

1. In the `develop` branch update the library version code to a new version so the `develop` branch code and the released code does not have a matching version.

```
git checkout develop 
git pull 
```

- Keep the release number in the style of X.Y.Z.

```
ext {
    // Current version of the library
    libraryVersion = "X.Y.Z"
}
```

and commit and push the changes:

```
git add build.gradle
git commit -m "Update the version code to X.Y.Z"
git push
```

## EXTRA: Manual publishing

1. Setup environment variables as described at https://labmobi.atlassian.net/wiki/spaces/DEV/pages/15990951/Sonatype+Maven+Access+Credentials

2. Run publishing via command line

   ```
   ./gradlew publishToMavenCentral --no-configuration-cache
   ```
