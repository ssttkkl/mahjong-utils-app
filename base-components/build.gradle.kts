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
    }
}

android {
    namespace = "$APPLICATION_ID.basecomponents"
}