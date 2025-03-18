package mahjongutils.buildlogic

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import mahjongutils.buildlogic.utils.downloadFile
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.enableWasm
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.apache.commons.io.FileUtils
import org.jetbrains.compose.web.WebExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

const val APPLICATION_ID = "io.ssttkkl.mahjongutils.app"
const val APPLICATION_NAME = "mahjong-utils-app"
const val APPLICATION_NAME_CAMEL = "RiichiMahjongCalculator"
const val APPLICATION_DISPLAY_NAME = "Riichi Mahjong Calculator"

class ComposeAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        listOf(
            "kotlinMultiplatform",
            "jetbrainsCompose",
            "compose-compiler"
        ).forEach {
            pluginManager.apply(libs.findPlugin(it).get().get().pluginId)
        }

        if (enableAndroid) {
            pluginManager.apply(libs.findPlugin("androidApplication").get().get().pluginId)
        }

        if (enableIos) {
            pluginManager.apply(libs.findPlugin("kotlinNativeCocoapods").get().get().pluginId)
        }

        val (versionName, versionCode) = readVersion()
        project.version = versionName

        configKotlinMultiplatform()
        if (enableIos) {
            configKotlinIos()
        }
        if (enableAndroid) {
            configAndroid()
            configAndroidSigning()
        }
        if (enableDesktop) {
            configComposeDesktop()
        }
        if (enableWasm) {
            configComposeWeb()
        }
    }

    private fun Project.configKotlinMultiplatform() {
        (kotlinExtension as KotlinMultiplatformExtension).apply {
            applyDefaultHierarchyTemplate()

            if (enableAndroid) {
                androidTarget()
                println("${project.name} target: android")
            }

            if (enableIos) {
                iosX64()
                println("${project.name} target: iosX64")
                iosArm64()
                println("${project.name} target: iosArm64")
                iosSimulatorArm64()
                println("${project.name} target: iosSimulatorArm64")
            }
            if (enableDesktop) {
                jvm("desktop")
                println("${project.name} target: desktop")
            }

            if (enableWasm) {
                @OptIn(ExperimentalWasmDsl::class)
                wasmJs()
                println("${project.name} target: wasmJs")
            }

            compilerOptions {
                optIn.add("org.jetbrains.compose.resources.ExperimentalResourceApi")
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }

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

    private fun Project.configKotlinIos() {
        val (versionName, versionCode) = readVersion()

        (kotlinExtension as KotlinMultiplatformExtension).apply {
            (this as ExtensionAware).extensions.configure<CocoapodsExtension> {
                version = versionName
                summary = APPLICATION_DISPLAY_NAME
                homepage = properties["opensource.repo"].toString()
                source =
                    "{ :git => '${properties["opensource.repo"].toString()}.git', :tag => '$version' }"
                license = properties["opensource.license"].toString()
                ios.deploymentTarget = "13.0"
                podfile = project.file("../iosApp/Podfile")

                framework {
                    baseName = project.name
                    isStatic = true
                    @Suppress("OPT_IN_USAGE")
                    transitiveExport = false
                }
            }
        }
    }

    private fun Project.configAndroid() {
        val (versionName, versionCode) = readVersion()

        (extensions.getByName("android") as BaseAppModuleExtension).apply {
            namespace = APPLICATION_ID
            compileSdk = libs.findVersion("android-compileSdk").get().toString().toInt()

            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            sourceSets["main"].res.srcDirs("src/androidMain/res")
            sourceSets["main"].resources.srcDirs("src/commonMain/resources")

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
        val localProperties = Properties()
        if (rootProject.file("local.properties").exists()) {
            rootProject.file("local.properties").inputStream().use { inputStream ->
                localProperties.load(inputStream)
            }
        }

        (extensions.getByName("android") as BaseAppModuleExtension).apply {
            signingConfigs {
                val keystoreFile = rootProject.file("keystore.jks")
                if (keystoreFile.exists()) {
                    create("release") {
                        storeFile = rootProject.file("keystore.jks")
                        storePassword =
                            localProperties["android.signing.release.storePassword"]?.toString()
                                ?: System.getenv("ANDROID_SIGNING_RELEASE_STORE_PASSWORD")
                        keyAlias = localProperties["android.signing.release.keyAlias"]?.toString()
                            ?: System.getenv("ANDROID_SIGNING_RELEASE_KEY_ALIAS")
                        keyPassword =
                            localProperties["android.signing.release.keyPassword"]?.toString()
                                ?: System.getenv("ANDROID_SIGNING_RELEASE_KEY_PASSWORD")
                    }
                }
            }
        }
    }

    private fun Project.configComposeDesktop() {
        val (versionName, versionCode) = readVersion()

        extensions.getByType<ComposeExtension>().extensions.getByType<DesktopExtension>().apply {
            application {
                mainClass = "MainKt"

                nativeDistributions {
                    packageName = APPLICATION_NAME
                    packageVersion = versionName
                    description = APPLICATION_DISPLAY_NAME
                    copyright = "Copyright (c) 2024 ssttkkl"
                    licenseFile.set(rootProject.file("LICENSE"))

                    windows {
                        iconFile.set(file("icon.ico"))
                        upgradeUuid = "16b7010f-44eb-4157-9113-3f8e44d72955"
                        shortcut = true
                        menu = true
                    }

                    macOS {
                        dockName = APPLICATION_DISPLAY_NAME
                        iconFile.set(file("icon.icns"))
                        bundleID = "io.ssttkkl.mahjongutils.app"
                    }

                    linux {
                        iconFile.set(file("icon.png"))
                    }

                    val hostOs = System.getProperty("os.name")
                    when {
                        hostOs == "Mac OS X" -> targetFormats(TargetFormat.Dmg)
                        hostOs == "Linux" -> targetFormats(TargetFormat.AppImage)
                        hostOs.startsWith("Windows") -> targetFormats(TargetFormat.Exe)
                    }
                }

                buildTypes.release.proguard {
                    configurationFiles.from(project.file("compose-common.pro"))
                }
            }
        }

        afterEvaluate {
            fun packAppImage(isRelease: Boolean) {
                val appDirSrc = project.file("${APPLICATION_NAME}.AppDir")
                val packageOutput = if (isRelease)
                    layout.buildDirectory.dir("compose/binaries/main-release/app/${APPLICATION_NAME}")
                        .get().asFile
                else
                    layout.buildDirectory.dir("compose/binaries/main/app/${APPLICATION_NAME}")
                        .get().asFile
                if (!appDirSrc.exists() || !packageOutput.exists()) {
                    return
                }

                val appimagetool = layout.buildDirectory.dir("tmp").get().asFile
                    .resolve("appimagetool-x86_64.AppImage")

                downloadFile(
                    "https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage",
                    appimagetool
                )

                if (!appimagetool.canExecute()) {
                    appimagetool.setExecutable(true)
                }

                val appDir = if (isRelease)
                    layout.buildDirectory.dir("appimage/main-release/${APPLICATION_NAME}.AppDir")
                        .get().asFile
                else
                    layout.buildDirectory.dir("appimage/main/${APPLICATION_NAME}.AppDir").get().asFile
                if (appDir.exists()) {
                    appDir.deleteRecursively()
                }
                FileUtils.copyDirectory(appDirSrc, appDir)
                FileUtils.copyDirectory(packageOutput, appDir)

                val appExecutable = appDir.resolve("bin/${APPLICATION_NAME}")
                if (!appExecutable.canExecute()) {
                    appimagetool.setExecutable(true)
                }

                exec {
                    workingDir = appDir.parentFile
                    executable = appimagetool.canonicalPath
                    environment("ARCH", "x86_64")  // TODO: 支持arm64
                    args(
                        "${APPLICATION_NAME}.AppDir",
                        "${APPLICATION_NAME_CAMEL}-${versionName}-x86_64.AppImage"
                    )
                }
            }

            tasks.findByName("packageAppImage")?.doLast {
                packAppImage(false)
            }
            tasks.findByName("packageReleaseAppImage")?.doLast {
                packAppImage(true)
            }
        }
    }

    private fun Project.configComposeWeb() {
        (kotlinExtension as KotlinMultiplatformExtension).apply {
            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                moduleName = APPLICATION_NAME
                browser {
                    commonWebpackConfig {
                        outputFileName = "${APPLICATION_NAME}.js"
                    }
                }
                binaries.executable()
            }
        }

        extensions.getByType<ComposeExtension>().extensions.getByType<WebExtension>().apply {

        }
    }
}
