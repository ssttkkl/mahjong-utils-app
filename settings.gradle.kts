import java.util.Properties

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
include(":mahjong-detector")
include(":shared")

if (props.isEmpty || props["ENABLE_ANDROID"]?.toString()?.toBoolean() != false) {
    include(":composeApp")
}
if (props.isEmpty || props["ENABLE_DESKTOP"]?.toString()?.toBoolean() != false) {
    include(":desktopApp")
}
if (props.isEmpty || props["ENABLE_WASM"]?.toString()?.toBoolean() != false) {
    include(":webApp")
}

