import com.android.build.gradle.BaseExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.OnnxRuntimeLibraryFilter
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.readGitCommitHash
import mahjongutils.buildlogic.utils.readVersion
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension

plugins {
    id("mahjongutils.buildlogic.lib")
    id("mahjongutils.buildlogic.ios.framework")
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":base-components"))
                api(project(":mahjong-detector"))

                api(compose.components.resources)

                api(libs.about.libraries.core)
                api(libs.about.libraries.compose)

                api(libs.voyager.navigator)
                api(libs.voyager.screenmodel)

                api(libs.filekit.core)
                api(libs.filekit.dialogs.compose)
                api(libs.krop.ui)

                api(libs.mahjong.utils)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        if (enableIos) {
            extensions.getByType<CocoapodsExtension>().apply {
                framework {
                    export(project(":mahjong-detector"))
                }
            }
        }
    }
}

if (enableAndroid) {
    (extensions.findByName("android") as BaseExtension?)?.apply {
        namespace = "$APPLICATION_ID.shared"
    }
}

compose {
    resources {
        packageOfResClass = "mahjongutils.composeapp.generated.resources"
        publicResClass = true
    }
}

buildkonfig {
    packageName = APPLICATION_ID
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        val (versionName, versionCode) = readVersion()
        buildConfigField(STRING, "APPLICATION_ID", APPLICATION_ID)
        buildConfigField(STRING, "VERSION_NAME", versionName)
        buildConfigField(STRING, "VERSION_CODE", versionCode.toString())
        buildConfigField(STRING, "OPENSOURCE_REPO", properties["opensource.repo"].toString())
        buildConfigField(STRING, "OPENSOURCE_LICENSE", properties["opensource.license"].toString())
        buildConfigField(STRING, "GIT_COMMIT_HASH", readGitCommitHash())
        buildConfigField(STRING, "SENTRY_DSN", properties["io.sentry.dsn"].toString())
    }
}

aboutLibraries {
    // 移除 "generated" 时间戳
    export.excludeFields.add("generated")
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