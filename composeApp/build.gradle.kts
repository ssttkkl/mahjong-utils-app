import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import dev.icerock.gradle.MRVisibility
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinxAtomicfu)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinNativeCocoapods)
    alias(libs.plugins.mokoResources)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    applyDefaultHierarchyTemplate()

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "Riichi Mahjong Calculator"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "riichiMahjongCalculator.js"
//            }
//        }
//        binaries.executable()
//    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm("desktop")

    cocoapods {
        version = "1.0.0"
        summary = "Riichi Mahjong Calculator"
        homepage = "https://github.com/NNSZ-Yorozuya/mahjong-utils-app"
        source =
            "{ :git => 'https://github.com/NNSZ-Yorozuya/mahjong-utils-app.git', :tag => '$version' }"
        license = "Private"
        ios.deploymentTarget = "13.0"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = project.name
            isStatic = true
            @Suppress("OPT_IN_USAGE")
            transitiveExport = false
        }
    }

    sourceSets {
        // https://github.com/icerockdev/moko-resources/issues/618
        getByName("androidMain").dependsOn(commonMain.get())
        getByName("desktopMain").dependsOn(commonMain.get())
        getByName("iosArm64Main").dependsOn(commonMain.get())
        getByName("iosX64Main").dependsOn(commonMain.get())
        getByName("iosSimulatorArm64Main").dependsOn(commonMain.get())

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.material3.windowSizeClass)

            implementation(libs.about.libraries.core)
            implementation(libs.about.libraries.compose)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)

            implementation(libs.moko.resources)
            implementation(libs.moko.resources.compose)

            implementation(libs.okio)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.core.okio)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.json.okio)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.atomicfu)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.mahjong.utils)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.appdirs)
            }
        }
    }
}

android {
    namespace = "io.ssttkkl.mahjongutils.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.ssttkkl.mahjongutils.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = properties["version.code"].toString().toInt()
        versionName = properties["version.name"].toString()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            packageName = "mahjong-utils-app"
            packageVersion = properties["version.name"].toString()
            licenseFile.set(rootProject.file("LICENSE"))

            modules("java.base", "java.instrument", "jdk.unsupported", "jdk.xml.dom")

            val hostOs = System.getProperty("os.name")
            when {
                hostOs == "Mac OS X" -> targetFormats(TargetFormat.Dmg)
                hostOs == "Linux" -> targetFormats(TargetFormat.AppImage)
                hostOs.startsWith("Windows") -> targetFormats(TargetFormat.Exe)
            }
        }
    }
}


//compose.experimental {
//    web.application {}
//}

multiplatformResources {
    multiplatformResourcesPackage = "io.ssttkkl.mahjongutils.app"
    multiplatformResourcesVisibility = MRVisibility.Internal
    iosBaseLocalizationRegion = "zh"
}

buildkonfig {
    packageName = "io.ssttkkl.mahjongutils.app"

    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", properties["version.name"].toString())
        buildConfigField(STRING, "VERSION_CODE", properties["version.code"].toString())
    }
}