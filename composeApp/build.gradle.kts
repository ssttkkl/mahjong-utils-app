import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.apache.commons.io.FileUtils
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties

plugins {
    val enableAndroid = System.getProperty("enable_android")
        ?.equals("true", ignoreCase = true) != false
            && JavaVersion.current() >= JavaVersion.VERSION_17

    val enableIos = System.getProperty("enable_ios")
        ?.equals("true", ignoreCase = true) != false

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication) apply enableAndroid
    alias(libs.plugins.kotlinNativeCocoapods) apply enableIos
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinxAtomicfu)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.undercouch.download)
}

// vercel自带的Java 11，但是AGP要求17，所以添加开关
val enableAndroid = System.getProperty("enable_android")
    ?.equals("true", ignoreCase = true) != false
        && JavaVersion.current() >= JavaVersion.VERSION_17

val enableIos = System.getProperty("enable_ios")
    ?.equals("true", ignoreCase = true) != false

val enableDesktop = System.getProperty("enable_desktop")
    ?.equals("true", ignoreCase = true) != false

val enableWeb = System.getProperty("enable_web")
    ?.equals("true", ignoreCase = true) != false

val localProperties = Properties()
if (rootProject.file("local.properties").exists()) {
    rootProject.file("local.properties").inputStream().use { inputStream ->
        localProperties.load(inputStream)
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    if (enableAndroid) {
        androidTarget()
        println("target: android")
    }

    if (enableIos) {
        iosX64()
        println("target: iosX64")
        iosArm64()
        println("target: iosArm64")
        iosSimulatorArm64()
        println("target: iosSimulatorArm64")
    }

    if (enableDesktop) {
        jvm("desktop")
        println("target: desktop")
    }

    if (enableWeb) {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
        wasmJs {
            moduleName = "mahjong-utils-app"
            browser {
                commonWebpackConfig {
                    outputFileName = "composeApp.js"
                }
            }
            binaries.executable()
        }
        println("target: wasmJs")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.material3.windowSizeClass)

                implementation(libs.about.libraries.core)
                implementation(libs.about.libraries.compose)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)

                implementation(libs.okio)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.json.okio)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.mahjong.utils)
            }
        }

        val nonWasmJsMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.datastore.core)
                implementation(libs.androidx.datastore.core.okio)
            }
        }

        val desktopAndWasmJsMain by creating {
            dependsOn(commonMain)
        }

        if (enableAndroid) {
            val androidMain by getting {
                dependsOn(nonWasmJsMain)
                dependencies {
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.kotlinx.coroutines.android)
                }
            }
        }
        if (enableIos) {
            val iosMain by getting {
                dependsOn(nonWasmJsMain)
            }
        }

        if (enableDesktop) {
            val desktopMain by getting {
                dependsOn(nonWasmJsMain)
                dependsOn(desktopAndWasmJsMain)
                dependencies {
                    implementation(compose.desktop.currentOs)
                    implementation(libs.kotlinx.coroutines.swing)
                    implementation(libs.appdirs)
//                    implementation(libs.slf4j.api)
//                    implementation(libs.logback.classic)
                }
            }
        }

        if (enableWeb) {
            val wasmJsMain by getting {
                dependsOn(desktopAndWasmJsMain)
            }
        }
    }

    @Suppress("OPT_IN_USAGE")
    compilerOptions {
        optIn.add("org.jetbrains.compose.resources.ExperimentalResourceApi")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    if (enableIos) {
        cocoapods {
            version = properties["version.name"].toString()
            summary = "Riichi Mahjong Calculator"
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

buildkonfig {
    packageName = "io.ssttkkl.mahjongutils.app"

    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", properties["version.name"].toString())
        buildConfigField(STRING, "VERSION_CODE", properties["version.code"].toString())
        buildConfigField(STRING, "OPENSOURCE_REPO", properties["opensource.repo"].toString())
        buildConfigField(STRING, "OPENSOURCE_LICENSE", properties["opensource.license"].toString())
    }
}

if (enableAndroid) {
    (extensions.getByName("android") as BaseAppModuleExtension).apply {
        namespace = "io.ssttkkl.mahjongutils.app"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
        sourceSets["main"].res.srcDirs("src/androidMain/res")
        sourceSets["main"].resources.srcDirs("src/commonMain/resources")

        defaultConfig {
            applicationId = "io.ssttkkl.mahjongutils.app"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionName = properties["version.name"].toString()

            val codeInVersionName = versionName!!.split(".").map { it.toInt() }
            versionCode =
                codeInVersionName[0] * 10000 + codeInVersionName[1] * 100 + codeInVersionName[2]
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
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
                    keyPassword = localProperties["android.signing.release.keyPassword"]?.toString()
                        ?: System.getenv("ANDROID_SIGNING_RELEASE_KEY_PASSWORD")
                }
            }
        }
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                signingConfig = signingConfigs.findByName("release")
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

if (enableDesktop) {
    compose.desktop {
        application {
            mainClass = "MainKt"

            nativeDistributions {
                packageName = "mahjong-utils-app"
                packageVersion = properties["version.name"].toString()
                description = "Riichi Mahjong Calculator"
                copyright = "Copyright (c) 2024 ssttkkl"
                licenseFile.set(rootProject.file("LICENSE"))

                val hostOs = System.getProperty("os.name")
                when {
                    hostOs == "Mac OS X" -> targetFormats(TargetFormat.Dmg)
                    hostOs == "Linux" -> targetFormats(TargetFormat.AppImage)
                    hostOs.startsWith("Windows") -> targetFormats(TargetFormat.Exe)
                }

                windows {
                    iconFile.set(file("icon.ico"))
                    upgradeUuid = "16b7010f-44eb-4157-9113-3f8e44d72955"
                    shortcut = true
                    menu = true
                }

                macOS {
                    dockName = "Riichi Mahjong Calculator"
                    iconFile.set(file("icon.icns"))
                    bundleID = "io.ssttkkl.mahjongutils.app"
                }

                linux {
                    iconFile.set(file("icon.png"))
                }
            }
        }
    }

    afterEvaluate {
        tasks.findByName("packageAppImage")?.doLast {
            val appDirSrc = project.file("mahjong-utils-app.AppDir")
            val packageOutput =
                layout.buildDirectory.dir("compose/binaries/main/app/mahjong-utils-app")
                    .get().asFile
            if (!appDirSrc.exists() || !packageOutput.exists()) {
                return@doLast
            }

            val downloadDest = layout.buildDirectory.dir("tmp").get().asFile
            val appimagetool = downloadDest.resolve("appimagetool-x86_64.AppImage")

            download.run {
                src("https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage")
                dest(downloadDest)
                overwrite(false)
            }

            if (!appimagetool.canExecute()) {
                appimagetool.setExecutable(true)
            }

            val appDir = layout.buildDirectory.dir("appimage/mahjong-utils-app.AppDir").get().asFile
            if (appDir.exists()) {
                appDir.deleteRecursively()
            }
            FileUtils.copyDirectory(appDirSrc, appDir)
            FileUtils.copyDirectory(packageOutput, appDir)

            val appExecutable = appDir.resolve("bin/mahjong-utils-app")
            if (!appExecutable.canExecute()) {
                appimagetool.setExecutable(true)
            }

            exec {
                workingDir = appDir.parentFile
                executable = appimagetool.canonicalPath
                environment("ARCH", "x86_64")  // TODO: 支持arm64
                args("mahjong-utils-app.AppDir", "mahjong-utils-app.AppImage")
            }
        }
    }
}

if (enableWeb) {
    compose.web {}
}
