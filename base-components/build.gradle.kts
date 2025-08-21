import com.android.build.gradle.BaseExtension
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop

plugins {
    id("mahjongutils.buildlogic.lib")
    id("mahjongutils.buildlogic.compose")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                api(compose.components.resources)
                api(libs.material3.windowSizeClass)
                api(libs.material.icons.core)

                api(project(":third-party:capturable"))
                api(project(":third-party:feather"))
                api(libs.okio)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.serialization.json.okio)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.atomicfu)
                api(libs.kotlinx.coroutines.core)

                api(libs.krop.ui)
            }
        }

        val nonWasmJsMain by getting {
            dependencies {
                api(libs.androidx.datastore.core)
                api(libs.androidx.datastore.core.okio)
            }
        }

        if (enableAndroid) {
            val androidMain by getting {
                dependencies {
                    api(libs.compose.ui.tooling.preview)
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
                    api(libs.appdirs)
                    api(libs.slf4j.api)
                    api(libs.logback.classic)
                }
            }
        }
    }
}

if (enableAndroid) {
    extensions.getByType<BaseExtension>().apply {
        namespace = "$APPLICATION_ID.basecomponents"
    }
}