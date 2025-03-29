import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.enableWasm
import mahjongutils.buildlogic.utils.readVersion

plugins {
    id("mahjongutils.buildlogic.composeapp")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinxAtomicfu)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
    id("dev.hydraulic.conveyor") version "1.12"
}

kotlin {
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
}

dependencies {
    // Use the configurations created by the Conveyor plugin to tell Gradle/Conveyor where to find the artifacts for each platform.
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

// region Work around temporary Compose bugs.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}
// endregion

tasks.register<Exec>("conveyMacAppArm64") {
    group = "conveyor"

    val dir = layout.buildDirectory.dir("packages")
    outputs.dir(dir)
    commandLine(
        "/Applications/Conveyor.app/Contents/MacOS/conveyor",
        "-Kapp.machines=mac.aarch64",
        "make",
        "--output-dir", dir.get(),
        "mac-app"
    )
    dependsOn("proguardReleaseJars", "writeConveyorConfig")
    environment("GITHUB_TOKEN", "111")
}
tasks.register<Exec>("conveyMacAppX64") {
    group = "conveyor"

    val dir = layout.buildDirectory.dir("packages")
    outputs.dir(dir)
    commandLine(
        "/Applications/Conveyor.app/Contents/MacOS/conveyor",
        "-Kapp.machines=mac.amd64",
        "make",
        "--output-dir", dir.get(),
        "mac-app"
    )
    dependsOn("proguardReleaseJars", "writeConveyorConfig")
    environment("GITHUB_TOKEN", "111")
}

buildkonfig {
    packageName = APPLICATION_ID

    defaultConfigs {
        val (versionName, versionCode) = readVersion()
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

