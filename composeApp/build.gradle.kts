import io.sentry.android.gradle.tasks.InjectSentryMetaPropertiesIntoAssetsTask

plugins {
    id("mahjongutils.buildlogic.app.android")
    alias(libs.plugins.sentryAndroid)
    alias(libs.plugins.sentryKotlinCompilerGradle)
}

dependencies {
    api(project(":shared"))
    api(project(":kuikly-android"))
    implementation(libs.compose.ui.tooling.preview)
}

sentry {
    org = "ssttkkl"
    projectName = "mahjong-utils-app"
    authToken = getLocalProperty("io.sentry.authToken")

    ignoredVariants.set(setOf("debug"))

    autoUploadProguardMapping = false
    autoUploadNativeSymbols = false
}

// 修复sentry注入debug-meta含有时间戳注释导致构建不可重复（F-droid要求）
afterEvaluate {
    (tasks.getByName("injectSentryDebugMetaPropertiesIntoAssetsRelease") as InjectSentryMetaPropertiesIntoAssetsTask).apply {
        doLast {
            val metaPropFile = outputDir.file("sentry-debug-meta.properties").get().asFile
            val content = metaPropFile.readLines()
                .filterNot { it.startsWith("#") }
                .joinToString("\n")
            metaPropFile.writeText(content)
        }
    }

}