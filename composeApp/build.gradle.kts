import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.apache.commons.io.FileUtils
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinNativeCocoapods) apply false
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinxAtomicfu)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.undercouch.download)
}

private fun ExtraPropertiesExtension.getBoolean(name: String, default: Boolean = true): Boolean {
    return if (has(name))
        get(name).toString().lowercase().toBooleanStrict()
    else
        default
}

// vercel自带的Java 11，但是AGP要求17，所以添加开关
val enableAndroid
    get() = rootProject.extra.getBoolean("ENABLE_ANDROID")
            && JavaVersion.current() >= JavaVersion.VERSION_17

if (enableAndroid) {
    apply(plugin = libs.plugins.androidApplication.get().pluginId)
}

val enableIos
    get() = rootProject.extra.getBoolean("ENABLE_IOS")
            && System.getProperty("os.name").startsWith("Mac")

if (enableIos) {
    apply(plugin = libs.plugins.kotlinNativeCocoapods.get().pluginId)
}

val enableDesktop
    get() = rootProject.extra.getBoolean("ENABLE_DESKTOP")

val enableWasm
    get() = rootProject.extra.getBoolean("ENABLE_WASM")

val localProperties = Properties()
if (rootProject.file("local.properties").exists()) {
    rootProject.file("local.properties").inputStream().use { inputStream ->
        localProperties.load(inputStream)
    }
}

val versionProperties = Properties()
file("version.properties").inputStream().use { inputStream ->
    versionProperties.load(inputStream)
}

val versionName = versionProperties["versionName"].toString()
val versionCode = versionProperties["versionCode"].toString().toInt()

kotlin {
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
        println("${project.name} target: wasmJs")
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

                implementation(libs.capturable)
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

        val nonAndroidMain by creating {
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
                dependsOn(nonAndroidMain)
            }
        }

        if (enableDesktop) {
            val desktopMain by getting {
                dependsOn(nonWasmJsMain)
                dependsOn(nonAndroidMain)
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

        if (enableWasm) {
            val wasmJsMain by getting {
                dependsOn(desktopAndWasmJsMain)
                dependsOn(nonAndroidMain)
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
        (this as ExtensionAware).extensions.configure<CocoapodsExtension> {
            version = versionName
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
        buildConfigField(STRING, "VERSION_NAME", versionName)
        buildConfigField(STRING, "VERSION_CODE", versionCode.toString())
        buildConfigField(STRING, "OPENSOURCE_REPO", properties["opensource.repo"].toString())
        buildConfigField(STRING, "OPENSOURCE_LICENSE", properties["opensource.license"].toString())
    }
}

aboutLibraries {
    // 移除 "generated" 时间戳
    excludeFields = arrayOf("generated")
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
        }
        defaultConfig.versionName = versionName
        defaultConfig.versionCode = versionCode
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
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    project.file("compose-common.pro"),
                    project.file("compose-r8.pro")
                )

                signingConfig = signingConfigs.findByName("release")
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        dependenciesInfo {
            // Disables dependency metadata when building APKs.
            includeInApk = false
            // Disables dependency metadata when building Android App Bundles.
            includeInBundle = false
        }

    }
}

if (enableDesktop) {
    compose.desktop {
        application {
            mainClass = "MainKt"

            nativeDistributions {
                packageName = "mahjong-utils-app"
                packageVersion = versionName
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

            buildTypes.release.proguard {
                configurationFiles.from(project.file("compose-common.pro"))
            }
        }
    }

    afterEvaluate {
        fun packAppImage(isRelease: Boolean) {
            val appDirSrc = project.file("mahjong-utils-app.AppDir")
            val packageOutput = if (isRelease)
                layout.buildDirectory.dir("compose/binaries/main-release/app/mahjong-utils-app")
                    .get().asFile
            else
                layout.buildDirectory.dir("compose/binaries/main/app/mahjong-utils-app")
                    .get().asFile
            if (!appDirSrc.exists() || !packageOutput.exists()) {
                return
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

            val appDir = if (isRelease)
                layout.buildDirectory.dir("appimage/main-release/mahjong-utils-app.AppDir")
                    .get().asFile
            else
                layout.buildDirectory.dir("appimage/main/mahjong-utils-app.AppDir").get().asFile
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
                args(
                    "mahjong-utils-app.AppDir",
                    "mahjong-utils-app-${versionName}.AppImage"
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

if (enableWasm) {
    compose.web {}
}
