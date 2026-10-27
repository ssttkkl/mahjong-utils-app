import io.sentry.android.gradle.tasks.InjectSentryMetaPropertiesIntoAssetsTask

plugins {
    id("mahjongutils.buildlogic.app.android")
    alias(libs.plugins.sentryAndroid)
    alias(libs.plugins.sentryKotlinCompilerGradle)
}

dependencies {
    api(project(":shared"))
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

// 移除时间戳注释并固定属性顺序，保证不同JDK生成的Sentry元数据一致（F-droid要求）
afterEvaluate {
    (tasks.getByName("injectSentryDebugMetaPropertiesIntoAssetsRelease") as InjectSentryMetaPropertiesIntoAssetsTask).apply {
        doLast {
            val metaPropFile = outputDir.file("sentry-debug-meta.properties").get().asFile
            val content = metaPropFile.readLines()
                .filterNot { it.startsWith("#") }
                .sorted()
                .joinToString("\n")
            metaPropFile.writeText(content)
        }
    }

}
