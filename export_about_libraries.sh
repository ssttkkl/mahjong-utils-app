#!/usr/bin/env bash
./gradlew :third-party:dummy-for-aboutlibraries:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files/ -PaboutLibraries.exportVariant=release
