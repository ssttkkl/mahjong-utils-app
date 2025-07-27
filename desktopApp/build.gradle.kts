import mahjongutils.buildlogic.utils.OnnxRuntimeLibraryFilter
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

// 去掉非本平台的动态库
dependencies {
    listOf("linux-aarch64", "linux-x64", "osx-aarch64", "osx-x64", "win-x64").forEach {
        registerTransform(OnnxRuntimeLibraryFilter::class.java) {
            parameters.platform.set(it)
            from.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "jar")
            to.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "onnxruntime-${it}-jar")
        }
    }
}