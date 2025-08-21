import com.android.build.gradle.BaseExtension
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid

plugins {
    id("mahjongutils.buildlogic.kmp.lib")
    id("mahjongutils.buildlogic.compose")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
            }
        }
    }
}

if (enableAndroid) {
    extensions.getByType<BaseExtension>().apply {
        namespace = "$APPLICATION_ID.thirdparty.capturable"
    }
}