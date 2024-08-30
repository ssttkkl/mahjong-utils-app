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

if (file("external/mahjong-utils/build.gradle.kts").exists()) {
    includeBuild("external/mahjong-utils") {
        name = "mahjong-utils-lib"
        dependencySubstitution {
            substitute(module("io.github.ssttkkl:mahjong-utils")).using(project(":mahjong-utils"))
        }
    }
}
includeBuild("external/Capturable") {
    dependencySubstitution {
        substitute(module("dev.shreyaspatil:capturable")).using(project(":capturable"))
    }
}
