# Publishing the Scrolls Logging Library

The Scrolls Logging Library is currently published to both Bintray (see https://bintray.com/mobilab/mobi.lab.scrolls/scrolls-android) and JCenter (https://bintray.com/bintray/jcenter?filterByPkgName=scrolls-android).

## Publishing to Bintray and JCenter

DISCLAIMER: These instructions are for the full manual publishing flow. If possible prefer to use the Scrolls build flow at Nevercode CI under Mobi Lab's account. See `RELEASE_GUIDE.md` for more.

### Prerequisites

1) Access to the Mobi Lab organization at https://bintray.com/mobilab along with artifact upload permissions. 

### Publishing via Gradle

WARNING: Publishing should only and always be done from the `master` branch!

1) Create or update (rename the "_TEMPLATE" file) your `publish.properties` file in the project root folder. Add the following properties and fill them with the correct values:

```properties
# Your developer id
# use the Bintray username for example
bintray_developer_id=
# Your developer name
bintray_developer_name=
# Your developer email
bintray_developer_email=
# Your Bintray username here
bintray_user=
# Your Bintray API key here
bintray_apiKey=
# Your Bintray organization
bintray_organization=mobilab #Use "Mobilab" to upload Scrolls
# Should this be a test / dryrun or an actual publish.
# dryRuns are good for testing
# (Optional, false by default)
bintray_dry_run=false
```

2) Build the project and make sure everything works

```bash
./gradlew buildAllRelease
```

3) Publish the Scrolls library to Bintray and JCenter via:

```bash
./gradlew install bintrayUpload
```

or using the custom target:

```
./gradlew buildAndPublishLibRelease
```

The latter will do both the release build and the publishing in one go.

4) Open the Scrolls repository at https://bintray.com/mobilab/mobi.lab.scrolls/scrolls-android and make sure the new version is visible and was published correctly