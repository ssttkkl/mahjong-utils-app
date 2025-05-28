package mahjongutils.buildlogic

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import getLocalProperty
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

class AndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        if (!enableAndroid) {
            logger.lifecycle("You must enable Android build")
            return
        }

        pluginManager.apply(libs.findPlugin("androidApplication").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("kotlinAndroid").get().get().pluginId)

        val (versionName, versionCode) = readVersion()
        project.version = versionName

        configKotlin()
        configAndroid()
        configAndroidSigning()
    }

    private fun Project.configKotlin() {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(
                    JvmTarget.valueOf(
                        "JVM_" + libs.findVersion("java-targetJvm").get().toString()
                    )
                )
            }
        }
    }

    private fun Project.configAndroid() {
        val (versionName, versionCode) = readVersion()

        (extensions.getByName("android") as BaseAppModuleExtension).apply {
            namespace = APPLICATION_ID
            compileSdk = libs.findVersion("android-compileSdk").get().toString().toInt()

            sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")
            sourceSets["main"].res.srcDirs("src/main/res")
            sourceSets["main"].resources.srcDirs("src/main/resources")

            defaultConfig {
                minSdk = libs.findVersion("android-minSdk").get().toString().toInt()
                targetSdk = libs.findVersion("android-targetSdk").get().toString().toInt()
                applicationId = APPLICATION_ID
            }
            defaultConfig.versionName = versionName
            defaultConfig.versionCode = versionCode
            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
            buildTypes {
                getByName("release") {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        project.file("compose-common.pro"),
                        project.file("compose-r8.pro")
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
            dependenciesInfo {
                // Disables dependency metadata when building APKs.
                includeInApk = false
                // Disables dependency metadata when building Android App Bundles.
                includeInBundle = false
            }

        }
    }

    private fun Project.configAndroidSigning() {
        (extensions.getByName("android") as BaseAppModuleExtension).apply {
            signingConfigs {
                val keystoreFile = rootProject.file("keystore.jks")
                if (keystoreFile.exists()) {
                    create("release") {
                        storeFile = rootProject.file("keystore.jks")
                        storePassword = getLocalProperty("android.signing.release.storePassword")
                        keyAlias = getLocalProperty("android.signing.release.keyAlias")
                        keyPassword = getLocalProperty("android.signing.release.keyPassword")
                    }
                }
            }
            buildTypes {
                named("release") {
                    signingConfig = signingConfigs.findByName("release")
                }
            }
        }
    }
}
