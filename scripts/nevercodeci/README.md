# Nevercode pre-build configuration script

A helper script to run this build in the Nevercode CI (https://www.nevercode.io).

## Usage

In order to execute the build in Nevercode the `publish.properties` file must be correctly set up. The script handles this setup.

The script must me added via Nevercode's *Environment* -> *Environment Files* section. All parameters need to be specified via *Environment Variables*. In Nevercode this can be done from *Environment* -> *Environment Variables* section.

### Script setup

The `nevercode_post_clone_script.sh` script should be added to Nevercode build via the Nevercode's *Environment* -> *Environment Files* section with the variable name of  `NC_POST_CLONE_SCRIPT`:

```bash
NC_POST_CLONE_SCRIPT nevercode_post_clone_script.sh
```

It will execute after cloning the project repository and before executing any Gradle specific tasks.

### Required Nevercode environment variables

The following variables must be specified via the Nevercode's *Environment* -> *Environment Variables* section: 

```properties
bintray_developer_id {Your developer id}
```

```properties
bintray_developer_name {Your developer name}
```

```properties
bintray_developer_email {Your developer email}
```

```properties
bintray_user {Your bintray username}
```

```properties
bintray_api_key {Your bintray API key}
```

```properties
bintray_organization mobilab
```

### Optional Nevercode environment variables

The following variable are optional. These can be specified via the Nevercode's *Environment* -> *Environment Variables* section: 

```properties
bintray_dry_run {true|false}
```

(for `bintray_dry_run` the default value is `false`)