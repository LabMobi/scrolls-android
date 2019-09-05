#!/bin/bash
# Use the "NC_POST_CLONE_SCRIPT" target in Nevercode CI to execute the script aftet the repository is cloned but before the project is built.


# Use the environment variable NEVERCODE_BUILD_DIR if it exists, otherwise default to current folder
[[ -z "${NEVERCODE_BUILD_DIR}" ]] && LOCAL_BUILD_DIR="`pwd`/" || LOCAL_BUILD_DIR="${NEVERCODE_BUILD_DIR}"

# Use the environment variable bintray_developer_id if it exists, otherwise default to ""
[[ -z "${bintray_developer_id}" ]] && LOCAL_DEVELOPER_ID='' || LOCAL_DEVELOPER_ID="${bintray_developer_id}"

# Use the environment variable bintray_developer_name if it exists, otherwise default to ""
[[ -z "${bintray_developer_name}" ]] && LOCAL_DEVELOPER_NAME='' || LOCAL_DEVELOPER_NAME="${bintray_developer_name}"

# Use the environment variable bintray_developer_email if it exists, otherwise default to ""
[[ -z "${bintray_developer_email}" ]] && LOCAL_DEVELOPER_EMAIL='' || LOCAL_DEVELOPER_EMAIL="${bintray_developer_email}"

# Use the environment variable bintray_user if it exists, otherwise default to ""
[[ -z "${bintray_user}" ]] && LOCAL_DEVELOPER_BINTRAY_USER='' || LOCAL_DEVELOPER_BINTRAY_USER="${bintray_user}"

# Use the environment variable bintray_api_key if it exists, otherwise default to ""
[[ -z "${bintray_api_key}" ]] && LOCAL_DEVELOPER_BINTRAY_API_KEY='' || LOCAL_DEVELOPER_BINTRAY_API_KEY="${bintray_api_key}"

# Use the environment variable bintray_organization if it exists, otherwise default to ""
[[ -z "${bintray_organization}" ]] && LOCAL_ORGANIZATION='' || LOCAL_ORGANIZATION="${bintray_organization}"

# Use the environment variable bintray_dry_run if it exists, otherwise default to "false"
[[ -z "${bintray_dry_run}" ]] && LOCAL_IS_DRY_RUN='false' || LOCAL_IS_DRY_RUN="${bintray_dry_run}"


cd $LOCAL_BUILD_DIR

echo "POST CLONE: Preparing publish.properties"
echo "POST CLONE: Configuration is as follows:"
echo "POST CLONE: 	Nevercode build directory: $LOCAL_BUILD_DIR"
echo "POST CLONE: 	Developer id: $LOCAL_DEVELOPER_ID"
echo "POST CLONE: 	Developer name: $LOCAL_DEVELOPER_NAME"
echo "POST CLONE: 	Developer email: $LOCAL_DEVELOPER_EMAIL"
echo "POST CLONE: 	Organization: $LOCAL_ORGANIZATION"
echo "POST CLONE: 	Is Publish Dry Run: $LOCAL_IS_DRY_RUN"

# Check if we have required parameters set
if [ -z "${LOCAL_DEVELOPER_ID}" ]; then
    echo "POST CLONE: WARNING - LOCAL_DEVELOPER_ID is not set in ENV, Scrolls project will not be able to build."
	exit 1
fi

if [ -z "${LOCAL_DEVELOPER_NAME}" ]; then
    echo "POST CLONE: WARNING - LOCAL_DEVELOPER_NAME is not set in ENV, Scrolls project will not be able to build."
	exit 1
fi

if [ -z "${LOCAL_DEVELOPER_EMAIL}" ]; then
    echo "POST CLONE: WARNING - LOCAL_DEVELOPER_EMAIL is not set in ENV, Scrolls project will not be able to build."
	exit 1
fi

if [ -z "${LOCAL_ORGANIZATION}" ]; then
    echo "POST CLONE: WARNING - LOCAL_ORGANIZATION is not set in ENV, Scrolls project will not be able to build."
	exit 1
fi

if [ -z "${LOCAL_DEVELOPER_BINTRAY_USER}" ]; then
    echo "POST CLONE: WARNING - LOCAL_DEVELOPER_BINTRAY_USER is not set in ENV, Scrolls project will not be able to build."
	exit 1
fi

if [ -z "${LOCAL_DEVELOPER_BINTRAY_API_KEY}" ]; then
    echo "POST CLONE: WARNING - LOCAL_DEVELOPER_BINTRAY_API_KEY is not set in ENV, Scrolls project will not be able to build."
	exit 1
fi


if [ "$1" = "-dry" ] 
then
    echo "POST CLONE: Dry run completed. Exiting."
	exit 0
fi


cd $LOCAL_BUILD_DIR


# Set the publish.properties
printf "\nbintray_developer_id=" >> publish.properties
printf "${LOCAL_DEVELOPER_ID}" >> publish.properties

printf "\nbintray_developer_name=" >> publish.properties
printf "${LOCAL_DEVELOPER_NAME}" >> publish.properties

printf "\nbintray_developer_email=" >> publish.properties
printf "${LOCAL_DEVELOPER_EMAIL}" >> publish.properties

printf "\nbintray_user=" >> publish.properties
printf "${LOCAL_DEVELOPER_BINTRAY_USER}" >> publish.properties

printf "\nbintray_api_key=" >> publish.properties
printf "${LOCAL_DEVELOPER_BINTRAY_API_KEY}" >> publish.properties

printf "\nbintray_organization=" >> publish.properties
printf "${LOCAL_ORGANIZATION}" >> publish.properties

printf "\nbintray_dry_run=" >> publish.properties
printf "${LOCAL_IS_DRY_RUN}" >> publish.properties


echo "POST CLONE: We are done! Exiting."