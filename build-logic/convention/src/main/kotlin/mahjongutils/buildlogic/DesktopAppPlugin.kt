package mahjongutils.buildlogic

import mahjongutils.buildlogic.utils.downloadFile
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class DesktopAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        if (!enableDesktop) {
            logger.lifecycle("You must enable Desktop build")
            return
        }

        pluginManager.apply(libs.findPlugin("kotlinJvm").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("compose-compiler").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("jetbrainsCompose").get().get().pluginId)

        val (versionName, versionCode) = readVersion()
        project.version = versionName

        configKotlin()
        configComposeDesktop()
    }

    private fun Project.configKotlin() {
        kotlinExtension.jvmToolchain(libs.findVersion("java-targetJvm").get().toString().toInt())
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
                    vendor = "ssttkkl"
                    copyright = "Copyright (c) 2024 ssttkkl"
                    licenseFile.set(rootProject.file("LICENSE"))

                    windows {
                        iconFile.set(file("icon.ico"))
                        upgradeUuid = "16b7010f-44eb-4157-9113-3f8e44d72955"
                        shortcut = true
                        menu = true
                        dirChooser = true
                        perUserInstall = true
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
                    layout.buildDirectory.dir("appimage/main/${APPLICATION_NAME}.AppDir")
                        .get().asFile
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
                        "${APPLICATION_NAME}-${versionName}-x86_64.AppImage"
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
}
