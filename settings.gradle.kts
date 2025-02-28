rootProject.name = "mahjongutils"

pluginManagement {
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

//val mahjongUtilsLibPath = file("external/mahjong-utils")
//if (mahjongUtilsLibPath.resolve("build.gradle.kts").exists()) {
//    if (envPropFile.exists()) {
//        envPropFile.copyTo(mahjongUtilsLibPath.resolve("env.properties"), overwrite = true)
//    }
//    includeBuild(mahjongUtilsLibPath.path) {
//        name = "mahjong-utils-lib"
//        dependencySubstitution {
//            substitute(module("io.github.ssttkkl:mahjong-utils")).using(project(":mahjong-utils"))
//        }
//    }
//}

val capturableLibPath = file("external/Capturable")
if (envPropFile.exists()) {
    envPropFile.copyTo(capturableLibPath.resolve("env.properties"), overwrite = true)
}
includeBuild(capturableLibPath.path) {
    dependencySubstitution {
        substitute(module("dev.shreyaspatil:capturable")).using(project(":capturable"))
    }
}
