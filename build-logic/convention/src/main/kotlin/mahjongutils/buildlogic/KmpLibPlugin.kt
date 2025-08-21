package mahjongutils.buildlogic

import com.android.build.gradle.LibraryExtension
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class KmpLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        if (enableAndroid) {
            pluginManager.apply(libs.findPlugin("androidLibrary").get().get().pluginId)
        }

        pluginManager.apply(KmpPlugin::class.java)

        if (enableAndroid) {
            configAndroidLibrary()
        }
    }
}