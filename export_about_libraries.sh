#!/usr/bin/env bash
./gradlew composeApp:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files/ -PaboutLibraries.exportVariant=release
