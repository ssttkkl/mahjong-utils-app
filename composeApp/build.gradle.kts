import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import mahjongutils.buildlogic.APPLICATION_ID
import mahjongutils.buildlogic.utils.enableAndroid
import mahjongutils.buildlogic.utils.enableDesktop
import mahjongutils.buildlogic.utils.readGitCommitHash
import mahjongutils.buildlogic.utils.readVersion

plugins {
    id("mahjongutils.buildlogic.app")
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sentryAndroid)
    alias(libs.plugins.sentryKotlinCompilerGradle)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":base-components"))

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.material3.windowSizeClass)

                implementation(libs.about.libraries.core)
//                implementation(libs.about.libraries.compose)
                implementation(project(":third-party:aboutlibraries-compose"))

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)

                implementation(libs.mahjong.utils)
            }
        }

        if (enableAndroid) {
            val androidMain by getting {
                dependencies {
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.kotlinx.coroutines.android)
                }
            }
        }

        if (enableDesktop) {
            val desktopMain by getting {
                dependencies {
                    implementation(compose.desktop.currentOs)
                    implementation(libs.kotlinx.coroutines.swing)
                }
            }
        }
    }
}

buildkonfig {
    packageName = APPLICATION_ID

    defaultConfigs {
        val (versionName, versionCode) = readVersion()
        buildConfigField(STRING, "APPLICATION_ID", APPLICATION_ID)
        buildConfigField(STRING, "VERSION_NAME", versionName)
        buildConfigField(STRING, "VERSION_CODE", versionCode.toString())
        buildConfigField(STRING, "OPENSOURCE_REPO", properties["opensource.repo"].toString())
        buildConfigField(STRING, "OPENSOURCE_LICENSE", properties["opensource.license"].toString())
        buildConfigField(STRING, "GIT_COMMIT_HASH", readGitCommitHash())
        buildConfigField(STRING, "SENTRY_DSN", properties["io.sentry.dsn"].toString())
    }
}

aboutLibraries {
    // 移除 "generated" 时间戳
    excludeFields = arrayOf("generated")
}

afterEvaluate {
    configurations["iosX64CompilationDependenciesMetadata"].exclude("io.sentry", "sentry-kotlin-extensions")
    configurations["iosArm64CompilationDependenciesMetadata"].exclude("io.sentry", "sentry-kotlin-extensions")
    configurations["iosSimulatorArm64CompilationDependenciesMetadata"].exclude("io.sentry", "sentry-kotlin-extensions")
    configurations["iosX64TestCompilationDependenciesMetadata"].exclude("io.sentry", "sentry-kotlin-extensions")
    configurations["iosArm64TestCompilationDependenciesMetadata"].exclude("io.sentry", "sentry-kotlin-extensions")
    configurations["iosSimulatorArm64TestCompilationDependenciesMetadata"].exclude("io.sentry", "sentry-kotlin-extensions")
}

sentry {
    org = "ssttkkl"
    projectName = "mahjong-utils-app-android"
    authToken = getLocalProperty("io.sentry.authToken")

    ignoredVariants.set(setOf("debug"))

    includeSourceContext = true
}

afterEvaluate {
    tasks.named("sentryCollectSourcesRelease").configure {
        mustRunAfter("generateResourceAccessorsForAndroidMain")
        mustRunAfter("generateActualResourceCollectorsForAndroidMain")
    }
    tasks.named("generateSentryBundleIdRelease").configure {
        mustRunAfter("generateResourceAccessorsForAndroidMain")
        mustRunAfter("generateActualResourceCollectorsForAndroidMain")
    }
}