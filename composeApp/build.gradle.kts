import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.readVersion

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

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.material3.windowSizeClass)

                implementation(libs.about.libraries.core)
//                implementation(libs.about.libraries.compose)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)

                implementation(libs.okio)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.json.okio)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.mahjong.utils)
            }
        }

        val nonWasmJsMain by getting {
            dependencies {
                implementation(libs.androidx.datastore.core)
                implementation(libs.androidx.datastore.core.okio)
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
                    implementation(libs.appdirs)
//                    implementation(libs.slf4j.api)
//                    implementation(libs.logback.classic)
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
    excludeFields = arrayOf("generated")
}

