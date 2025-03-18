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

include(":composeApp")

val envPropFile = file("env.properties")

val capturableLibPath = file("external/Capturable")
if (envPropFile.exists()) {
    envPropFile.copyTo(capturableLibPath.resolve("env.properties"), overwrite = true)
}
includeBuild(capturableLibPath.path) {
    dependencySubstitution {
        substitute(module("dev.shreyaspatil:capturable")).using(project(":capturable"))
    }
}
