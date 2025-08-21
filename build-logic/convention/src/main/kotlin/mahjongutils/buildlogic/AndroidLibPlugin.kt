package mahjongutils.buildlogic

import com.android.build.gradle.LibraryExtension
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

internal fun Project.configAndroidLibrary() {
    val (versionName, versionCode) = readVersion()

    (extensions.getByName("android") as LibraryExtension).apply {
        compileSdk = libs.findVersion("android-compileSdk").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("android-minSdk").get().toString().toInt()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("consumer-rules.pro")
        }

        defaultConfig.versionName = versionName
        defaultConfig.versionCode = versionCode

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

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

class AndroidLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply(libs.findPlugin("androidLibrary").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("kotlinAndroid").get().get().pluginId)
        
        configAndroidLibrary()
    }
}