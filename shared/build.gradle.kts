import com.android.build.gradle.BaseExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.readGitCommitHash
import mahjongutils.buildlogic.utils.readVersion
import kotlin.apply

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

                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                api(compose.components.resources)
                api(libs.material3.windowSizeClass)

                api(libs.about.libraries.core)
//                api(libs.about.libraries.compose)
                api(project(":third-party:aboutlibraries-compose"))

                api(libs.voyager.navigator)
                api(libs.voyager.screenmodel)

                api(libs.mahjong.utils)
            }
        }

        if (enableAndroid) {
            val androidMain by getting {
                dependencies {
                    api(libs.androidx.activity.compose)
                    api(libs.kotlinx.coroutines.android)
                }
            }
        }

        if (enableDesktop) {
            val desktopMain by getting {
                dependencies {
                    api(compose.desktop.currentOs)
                    api(libs.kotlinx.coroutines.swing)
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
    excludeFields = arrayOf("generated")
}
