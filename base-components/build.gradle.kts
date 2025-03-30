import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop

plugins {
    id("mahjongutils.buildlogic.lib")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.material3.windowSizeClass)

                api(project(":third-party:capturable"))
                api(project(":third-party:feather"))
                api(libs.okio)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.serialization.json.okio)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.atomicfu)
                api(libs.kotlinx.coroutines.core)
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
                    implementation(libs.slf4j.api)
                    implementation(libs.logback.classic)
                }
            }
        }
    }
}

android {
    namespace = "$APPLICATION_ID.basecomponents"
}