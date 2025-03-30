import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid

plugins {
    id("mahjongutils.buildlogic.lib")
    alias(libs.plugins.aboutLibraries)
}

kotlin {
    sourceSets {
        if (enableAndroid) {
            val androidMain by getting {
                dependencies {
                    implementation(project(":composeApp"))
                    implementation("com.quadible:feather:1.0.0")
                    implementation("dev.shreyaspatil:capturable:2.1.0")
                    implementation("com.mikepenz:aboutlibraries-compose-m3:11.6.3")
                }
            }
        }
    }
}

android {
    namespace = APPLICATION_ID + ".thirdparty.dummy"
}

aboutLibraries {
    // 移除 "generated" 时间戳
    excludeFields = arrayOf("generated")
}

