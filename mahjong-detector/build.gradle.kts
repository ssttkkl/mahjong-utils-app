import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.OnnxRuntimeLibraryFilter
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.enableWasm

plugins {
    id("mahjongutils.buildlogic.lib")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":base-components"))

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)

                implementation(libs.filekit.core)

                implementation(libs.mahjong.utils)
            }
        }

        val skiaMain by creating {
            dependsOn(commonMain)
        }

        if (enableAndroid) {
            val androidMain by getting {
                dependencies {
                    implementation(libs.androidx.core.ktx)
                    implementation(libs.onnxruntime.android)
                }
            }
        }

        if (enableDesktop) {
            val desktopMain by getting {
                dependsOn(skiaMain)
                dependencies {
                    val hostOs = System.getProperty("os.name")
                    val arch = System.getProperty("os.arch")
                    val artifactTypeAttr = when {
                        hostOs == "Mac OS X" && arch == "aarch64" -> "onnxruntime-osx-aarch64-jar"
                        hostOs == "Mac OS X" && arch == "x86_64" -> "onnxruntime-osx-x64-jar"
                        hostOs == "Linux" && arch == "amd64" -> "onnxruntime-linux-x64-jar"
                        hostOs.startsWith("Windows") && arch == "amd64" -> "onnxruntime-win-x64-jar"
                        else -> error("Unsupported hostOs and arch: ${hostOs}, ${arch}")
                    }
                    val onnxRuntime = dependencies.create(libs.onnxruntime.jvm.get().let {
                        "${it.group}:${it.name}:${it.version}"
                    }) {
                        attributes {
                            attribute(
                                ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                                artifactTypeAttr
                            )
                        }
                    }
                    implementation(onnxRuntime)
                }
            }
        }

        if (enableWasm) {
            val wasmJsMain by getting {
                dependsOn(skiaMain)
                dependencies {
                    implementation(npm("@tensorflow/tfjs", "^4.22.0"))
                }
            }
        }

        if (enableIos) {
            val iosMain by getting {
                dependsOn(skiaMain)
            }
        }
    }
}

android {
    namespace = "$APPLICATION_ID.mahjong_detector"
}

// 去掉非本平台的动态库
dependencies {
    listOf("linux-aarch64", "linux-x64", "osx-aarch64", "osx-x64", "win-x64").forEach {
        registerTransform(OnnxRuntimeLibraryFilter::class.java) {
            parameters.platform.set(it)
            from.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "jar")
            to.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "onnxruntime-${it}-jar")
        }
    }
}