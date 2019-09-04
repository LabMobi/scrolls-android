# Scrolls logging library

A simple logging library that allows to use different log destinations, most importantly allows to log to a file. Also has tools to take care of file sharing, cleanup. Also to allows to browse log files in the integrating application itself. 

This library can be very easily used as s sub-Tree for Timber library, allowing easily configurable extended logging for debug versions.

## Getting Started

Look at the sample application for a basic setup of the library. 

Some things to keep in mind:

#### FileProvider configuration 

##### Define a provider

Since we need to send logs as file attachments, we need to create file Uris. To achieve these, we need to create a `FileProvider` within the integrating application.

Look at the sample application:
* **src/main/res/xml/file_provider_paths.xml**
* **src/main/AndroidManifest.xml**
* **src/main/java/mobi.lab.scrolls.sample/SampleApplication.java**

Look at the `<provider>` tag in sample application's `AndroidManifest.xml`. It also references `file_provider_paths.xml` for defining paths.
The library keeps the logs in the internal files dir by default, so the `/` default path should be used. 

##### LogPostImpl.configure changes

LogPostImpl.configure call now expects a `fileProviderAuthority` String which is defined in the `<provider>` tag in the manifest.

This has to be unique across all applications. So most likely `applicationId + postfix`. For example `mobi.lab.scrollssample.logprovider`.

The library needs the authority String to be able to request a File Uri from the provider associated with the authority.

#### Adding the library to a project

1) Add the artifact to your project. The binary is hosted at JCenter.

##### Gradle

Add the following to your main application module's `build.gradle` file:

```groovy
dependencies {
    implementation "mobi.lab.scrolls:scrolls-lib:2.0.4"
}
```

2) Update you application manifest, add the provider:

```
<?xml version="1.0" encoding="utf-8"?>
<manifest package="sample.application"
          xmlns:android="http://schemas.android.com/apk/res/android">

    <application>
        <activity
            android:name="mobi.lab.scrolls.activity.LogListActivity"
            android:label="Logs"
            android:taskAffinity="sample.application.loglist"
            android:theme="@style/AppTheme">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <activity
            android:name="mobi.lab.scrolls.activity.LogPostActivity"
            android:process=".LogPostProcess"
            android:theme="@style/AppTheme"/>
        <activity
            android:name="mobi.lab.scrolls.activity.LogReaderActivity"
            android:theme="@style/AppTheme"/>

        <provider
            android:name="mobi.lab.scrolls.ScrollsFileProvider"
            android:authorities="${applicationId}.logs"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider_paths"/>
        </provider>
    </application>
</manifest>
```

Notes:
* `LogListActivity` creates a new `Log` launcher icon to show logs
* `@style/AppTheme` styles the activities to better match the consuming application

## Building the library

To build the library use the command

```groovy
./gradlew buildAllRelease
```

The Scrolls library *.aar can be found under the `scrolls-android\scrolls-lib\build\outputs\aar` folder and the sample app is under the `scrolls-android\scrolls-sample-java\build\outputs\apk` folder.

