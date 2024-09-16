import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinxAtomicfu) apply false
    alias(libs.plugins.kotlinNativeCocoapods) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.undercouch.download) apply false
}

val envPropFile = file("env.properties")
if (envPropFile.exists()) {
    val props = Properties().apply {
        envPropFile.reader().use { rd ->
            load(rd)
        }
    }
    println("=== props: ${props}")
    props.forEach { (k, v) ->
        extra.set(k.toString(), v)
    }
}
