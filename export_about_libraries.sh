#!/usr/bin/env bash
rootDir=$(pwd)
./gradlew :third-party:dummy-for-aboutlibraries:exportLibraryDefinitions -PaboutLibraries.exportPath=$rootDir/composeApp/src/commonMain/composeResources/files/ -PaboutLibraries.exportVariant=release
