package mahjongutils.buildlogic

import com.android.build.gradle.LibraryExtension
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class LibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        if (enableAndroid) {
            pluginManager.apply(libs.findPlugin("androidLibrary").get().get().pluginId)
        }

        pluginManager.apply(KmpPlugin::class.java)

        if (enableAndroid) {
            configAndroid()
        }
    }

    private fun Project.configAndroid() {
        val (versionName, versionCode) = readVersion()

        (extensions.getByName("android") as LibraryExtension).apply {
            compileSdk = libs.findVersion("android-compileSdk").get().toString().toInt()

            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            sourceSets["main"].res.srcDirs("src/androidMain/res")
            sourceSets["main"].resources.srcDirs("src/commonMain/resources")

            defaultConfig {
                minSdk = libs.findVersion("android-minSdk").get().toString().toInt()
                targetSdk = libs.findVersion("android-targetSdk").get().toString().toInt()
            }
            defaultConfig.versionName = versionName
            defaultConfig.versionCode = versionCode
            compileOptions {
                sourceCompatibility = JavaVersion.valueOf(
                    "VERSION_" + libs.findVersion("java-targetJvm").get().toString()
                )
                targetCompatibility = JavaVersion.valueOf(
                    "VERSION_" + libs.findVersion("java-targetJvm").get().toString()
                )
            }

        }
    }
}