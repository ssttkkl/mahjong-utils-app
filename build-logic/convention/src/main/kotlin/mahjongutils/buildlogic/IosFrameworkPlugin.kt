package mahjongutils.buildlogic

import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.libs
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension

class IosFrameworkPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        if (!enableIos) {
            logger.lifecycle("You must enable iOS build")
            return
        }

        pluginManager.apply(libs.findPlugin("kotlinNativeCocoapods").get().get().pluginId)

        configKotlinIos()
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
}
