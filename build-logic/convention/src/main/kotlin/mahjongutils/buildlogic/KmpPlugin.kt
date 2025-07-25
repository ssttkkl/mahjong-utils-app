package mahjongutils.buildlogic

import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.enableWasm
import mahjongutils.buildlogic.utils.libs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

class KmpPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        listOf(
            "kotlinMultiplatform",
            "jetbrainsCompose",
            "compose-compiler",
            "kotlinSerialization",
            "kotlinxAtomicfu"
        ).forEach {
            pluginManager.apply(libs.findPlugin(it).get().get().pluginId)
        }

        configKotlinMultiplatform()
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
                wasmJs {
                    outputModuleName.set(project.name)
                    browser()
                }
                println("${project.name} target: wasmJs")
            }

            compilerOptions {
                optIn.add("org.jetbrains.compose.resources.ExperimentalResourceApi")
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }

            extensions.getByName<NamedDomainObjectContainer<KotlinSourceSet>>("sourceSets").apply {
                val commonMain = getByName("commonMain")
                val nonAndroidMain = create("nonAndroidMain") {
                    dependsOn(commonMain)
                }
                val mobileMain = create("mobileMain") {
                    dependsOn(commonMain)
                }
                val nonWasmJsMain = create("nonWasmJsMain") {
                    dependsOn(commonMain)
                }
                val desktopAndWasmJsMain = create("desktopAndWasmJsMain") {
                    dependsOn(commonMain)
                }
                if (enableAndroid) {
                    androidMain {
                        dependsOn(nonWasmJsMain)
                        dependsOn(mobileMain)
                    }
                }
                if (enableIos) {
                    iosMain {
                        dependsOn(nonWasmJsMain)
                        dependsOn(nonAndroidMain)
                        dependsOn(mobileMain)
                    }
                }

                if (enableDesktop) {
                    getByName("desktopMain").apply {
                        dependsOn(nonWasmJsMain)
                        dependsOn(nonAndroidMain)
                        dependsOn(desktopAndWasmJsMain)
                    }
                }

                if (enableWasm) {
                    wasmJsMain {
                        dependsOn(desktopAndWasmJsMain)
                        dependsOn(nonAndroidMain)
                    }
                }
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
}