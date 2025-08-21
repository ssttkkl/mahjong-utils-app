import java.util.Properties

rootProject.name = "mahjongutils"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        google()
        mavenCentral()
        mavenLocal()
    }
}

val props = Properties().apply {
    val envPropFile = file("env.properties")
    if (envPropFile.exists()) {
        envPropFile.reader().use { rd ->
            load(rd)
        }
    }
}
println("=== props: ${props}")

include(":third-party:dummy-for-aboutlibraries")
include(":third-party:capturable")
include(":third-party:feather")

include(":base-components")
include(":base-utils")
include(":mahjong-detector")
include(":shared")
include(":kuikly-shared")
include(":kuikly-android")

if (props.isEmpty || props["ENABLE_ANDROID"]?.toString()?.toBoolean() != false) {
    include(":composeApp")
}
if (props.isEmpty || props["ENABLE_DESKTOP"]?.toString()?.toBoolean() != false) {
    include(":desktopApp")
}
if (props.isEmpty || props["ENABLE_WASM"]?.toString()?.toBoolean() != false) {
    include(":webApp")
}

