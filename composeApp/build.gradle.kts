plugins {
    id("mahjongutils.buildlogic.app.android")
    alias(libs.plugins.sentryAndroid)
    alias(libs.plugins.sentryKotlinCompilerGradle)
}

dependencies {
    api(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
}

sentry {
    org = "ssttkkl"
    projectName = "mahjong-utils-app"
    authToken = getLocalProperty("io.sentry.authToken")

    ignoredVariants.set(setOf("debug"))

    includeSourceContext = true
}
