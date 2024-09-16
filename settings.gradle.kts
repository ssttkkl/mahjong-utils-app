import java.util.Properties

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

val envPropFile = file("env.properties")
if (envPropFile.exists()) {
    val props = Properties().apply {
        envPropFile.reader().use { rd ->
            load(rd)
        }
    }
    props.forEach { (k, v) ->
        extra.set(k.toString(), v)
    }
}

include(":composeApp")

val mahjongUtilsLibPath = file("external/mahjong-utils")
if (mahjongUtilsLibPath.resolve("build.gradle.kts").exists()) {
    if (envPropFile.exists()) {
        envPropFile.copyTo(mahjongUtilsLibPath.resolve("env.properties"), overwrite = true)
    }
    includeBuild(mahjongUtilsLibPath.path) {
        name = "mahjong-utils-lib"
        dependencySubstitution {
            substitute(module("io.github.ssttkkl:mahjong-utils")).using(project(":mahjong-utils"))
        }
    }
}

val capturableLibPath = file("external/mahjong-utils")
if (envPropFile.exists()) {
    envPropFile.copyTo(capturableLibPath.resolve("env.properties"), overwrite = true)
}
includeBuild(capturableLibPath.path) {
    dependencySubstitution {
        substitute(module("dev.shreyaspatil:capturable")).using(project(":capturable"))
    }
}
