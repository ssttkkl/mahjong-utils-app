import mahjongutils.buildlogic.utils.enableDesktop

plugins {
    id("mahjongutils.buildlogic.app.desktop")
    alias(libs.plugins.sentryJvm)
}

if (enableDesktop) {
    dependencies {
        api(project(":shared"))
    }
}

sentry {
    org = "ssttkkl"
    projectName = "mahjong-utils-app"
    authToken = getLocalProperty("io.sentry.authToken")

    includeSourceContext = true
}

afterEvaluate {
    (tasks["run"] as JavaExec).args("--disableSentry")
}