import com.android.build.gradle.BaseExtension
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import kotlin.apply

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
                implementation(libs.about.libraries.core)
            }
        }
    }
}

(extensions.findByName("android") as BaseExtension?)?.apply {
    namespace = "$APPLICATION_ID.thirdparty.capturable"
}