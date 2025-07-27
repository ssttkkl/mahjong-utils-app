package mahjongutils.buildlogic

import mahjongutils.buildlogic.utils.enableWasm
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.web.WebExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class WebAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        if (!enableWasm) {
            logger.lifecycle("You must enable Wasm build")
            return
        }

        pluginManager.apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("compose-compiler").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("jetbrainsCompose").get().get().pluginId)

        val (versionName, versionCode) = readVersion()
        project.version = versionName

        configKotlin()
        configComposeWeb()
    }

    private fun Project.configKotlin() {
        kotlinExtension.jvmToolchain(libs.findVersion("java-targetJvm").get().toString().toInt())
    }

    private fun Project.configComposeWeb() {
        (kotlinExtension as KotlinMultiplatformExtension).apply {
            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
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
