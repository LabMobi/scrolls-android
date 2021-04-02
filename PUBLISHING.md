# Publishing the Scrolls Logging Library

The Scrolls Logging Library is currently published to Sonatype OSSRH https://s01.oss.sonatype.org/.

For more info see the original ticket at https://issues.sonatype.org/browse/OSSRH-66630

## Publishing to Sonatype OSSRH

DISCLAIMER: These instructions are for the full manual publishing flow. If possible prefer to use the Scrolls build flow at Codemagic CI under Mobi Lab's account. See `RELEASE_GUIDE.md` for more.

### Prerequisites

1) Access to the Mobi Lab account at https://s01.oss.sonatype.org/.  For more see https://confluence.lab.mobi/display/DEV/Sonatype+Maven+Access+Credentials

### Publishing via Gradle

WARNING: Publishing should only and always be done from the `master` branch!

1) Create or update (rename the "_TEMPLATE" file) your `publish.properties` file in the project root folder. Add the following properties and fill them with the correct values:

```properties

```

2) Build the project and make sure everything works

```bash
./gradlew buildAllRelease
```

3) Publish the Scrolls library to Sonatype OSSRH via:

```
./gradlew buildAndPublishLibRelease
```

The latter will do both the release build and the publishing in one go.

4) Open the Scrolls repository at https://repo1.maven.org/maven2/mobi/lab/scrolls/scrolls/ and make sure the new version is visible and was published correctly