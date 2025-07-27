import com.android.build.gradle.BaseExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
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

                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                api(compose.components.resources)
                api(libs.material3.windowSizeClass)
                api(libs.material.icons.core)

                api(libs.about.libraries.core)
                api(libs.about.libraries.compose)

                api(libs.voyager.navigator)
                api(libs.voyager.screenmodel)

                api(libs.filekit.core)
                api(libs.filekit.dialogs.compose)
                api(
                    libs.cmp.image.pick.n.crop.get().let {
                        "${it.group}:${it.name}:${it.version}"
                    }
                ) {
                    // 这个包引了一堆opencv、ffmpeg之类的库，应该只是拍照用
                    // 但是我们拍照不走这个库
                    exclude(group = "org.bytedeco")

                    exclude(group = "androidx.compose.ui", module = "ui-test-junit4")
                }

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
