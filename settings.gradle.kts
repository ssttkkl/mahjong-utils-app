rootProject.name = "mahjongutils"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

include(":third-party:dummy-for-aboutlibraries")
include(":third-party:capturable")
include(":third-party:feather")

include(":base-components")
include(":mahjong-detector")
include(":composeApp")

