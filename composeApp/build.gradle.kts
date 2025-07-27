import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.OnnxRuntimeLibraryFilter
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.readVersion
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension

plugins {
    id("mahjongutils.buildlogic.app")
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":base-components"))
                api(project(":mahjong-detector"))

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.material3.windowSizeClass)
                implementation(libs.material.icons.core)

                implementation(libs.about.libraries.core)
                implementation(libs.about.libraries.compose.m3)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)

                implementation(libs.filekit.core)
                implementation(libs.filekit.dialogs.compose)
                implementation(
                    libs.cmp.image.pick.n.crop.get().let {
                        "${it.group}:${it.name}:${it.version}"
                    }
                ) {
                    // 这个包引了一堆opencv、ffmpeg之类的库，应该只是拍照用
                    // 但是我们拍照不走这个库
                    exclude(group = "org.bytedeco")

                    exclude(group = "androidx.compose.ui", module = "ui-test-junit4")
                }

                implementation(libs.mahjong.utils)
            }
        }

        if (enableAndroid) {
            val androidMain by getting {
                dependencies {
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.kotlinx.coroutines.android)
                }
            }
        }

        if (enableDesktop) {
            val desktopMain by getting {
                dependencies {
                    implementation(compose.desktop.currentOs)
                    implementation(libs.kotlinx.coroutines.swing)
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

buildkonfig {
    packageName = APPLICATION_ID

    defaultConfigs {
        val (versionName, versionCode) = readVersion()
        buildConfigField(STRING, "VERSION_NAME", versionName)
        buildConfigField(STRING, "VERSION_CODE", versionCode.toString())
        buildConfigField(STRING, "OPENSOURCE_REPO", properties["opensource.repo"].toString())
        buildConfigField(STRING, "OPENSOURCE_LICENSE", properties["opensource.license"].toString())
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