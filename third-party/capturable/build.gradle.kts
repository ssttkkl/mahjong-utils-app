import com.android.build.gradle.AppExtension
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import org.gradle.kotlin.dsl.getByType
import kotlin.apply

plugins {
    id("mahjongutils.buildlogic.lib")
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
    extensions.getByType<AppExtension>().apply {
        namespace = "$APPLICATION_ID.thirdparty.capturable"
    }
}