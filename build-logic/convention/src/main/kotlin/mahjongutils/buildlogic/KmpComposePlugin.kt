package mahjongutils.buildlogic

import mahjongutils.buildlogic.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class KmpComposePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        // Apply the base KMP plugin first
        pluginManager.apply(KmpPlugin::class.java)
        
        // Apply Compose-specific plugins
        listOf(
            "jetbrainsCompose",
            "compose-compiler"
        ).forEach {
            pluginManager.apply(libs.findPlugin(it).get().get().pluginId)
        }

        configComposeOptions()
    }

    private fun Project.configComposeOptions() {
        (kotlinExtension as KotlinMultiplatformExtension).apply {
            compilerOptions {
                optIn.add("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
    }
}