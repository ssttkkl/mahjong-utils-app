import com.android.build.gradle.BaseExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.tencent.kuikly.gradle.config.KuiklyConfig
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.OnnxRuntimeLibraryFilter
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableIos
import mahjongutils.buildlogic.utils.readGitCommitHash
import mahjongutils.buildlogic.utils.readVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension

plugins {
    id("mahjongutils.buildlogic.kmp.lib")
    id("mahjongutils.buildlogic.compose")
    id("com.tencent.kuikly-open.kuikly")
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":base-utils"))

                implementation(libs.kuikly.core)
                implementation(libs.kuikly.core.annotations)
                implementation(libs.kuikly.compose)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.kuikly.core.render.android)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

fun getPageName(): String {
    return (project.properties["pageName"] as? String) ?: ""
}

dependencies {
    compileOnly(libs.kuikly.core.ksp) {
        if (enableAndroid) {
            add("kspAndroid", this)
        }
        if (enableIos) {
            add("kspIosArm64", this)
            add("kspIosX64", this)
            add("kspIosSimulatorArm64", this)
        }
//        add("kspJs", this)
    }
}

ksp {
    arg( "pageName", getPageName())
}

//// Kuikly 插件配置
//configure<KuiklyConfig> {
//    // JS 产物配置
//    js {
//        // 构建产物名，与 KMM 插件 webpackTask#outputFileName 一致
//        outputName("nativevue")
//        // 可选：分包构建时的页面列表，如果为空则构建全部页面
//        // addSplitPage("route","home")
//    }
//}

if (enableAndroid) {
    (extensions.findByName("android") as BaseExtension?)?.apply {
        namespace = "$APPLICATION_ID.kuikly.shared"
    }
}

buildkonfig {
    packageName = "$APPLICATION_ID.kuikly"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        val (versionName, versionCode) = readVersion()
        buildConfigField(STRING, "APPLICATION_ID", APPLICATION_ID)
        buildConfigField(STRING, "VERSION_NAME", versionName)
        buildConfigField(STRING, "VERSION_CODE", versionCode.toString())
        buildConfigField(STRING, "OPENSOURCE_REPO", properties["opensource.repo"].toString())
        buildConfigField(STRING, "OPENSOURCE_LICENSE", properties["opensource.license"].toString())
        buildConfigField(STRING, "GIT_COMMIT_HASH", readGitCommitHash())
    }
}
