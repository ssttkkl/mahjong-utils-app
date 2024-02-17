#!/usr/bin/env bash
./gradlew composeApp:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/resources/MR/files/ -PaboutLibraries.exportVariant=release
