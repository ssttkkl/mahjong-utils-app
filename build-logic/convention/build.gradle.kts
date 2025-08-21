/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `kotlin-dsl`
}

group = "mahjongutils.buildlogic"
version = "0.0.1"

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    api(libs.commons.io)
}

gradlePlugin {
    plugins {
        register("ios-framework") {
            id = "mahjongutils.buildlogic.ios.framework"
            implementationClass = "mahjongutils.buildlogic.IosFrameworkPlugin"
        }
        register("android-app") {
            id = "mahjongutils.buildlogic.app.android"
            implementationClass = "mahjongutils.buildlogic.AndroidAppPlugin"
        }
        register("desktop-app") {
            id = "mahjongutils.buildlogic.app.desktop"
            implementationClass = "mahjongutils.buildlogic.DesktopAppPlugin"
        }
        register("web-app") {
            id = "mahjongutils.buildlogic.app.web"
            implementationClass = "mahjongutils.buildlogic.WebAppPlugin"
        }
        register("kmp-lib") {
            id = "mahjongutils.buildlogic.kmp.lib"
            implementationClass = "mahjongutils.buildlogic.KmpLibPlugin"
        }
        register("kmp") {
            id = "mahjongutils.buildlogic.kmp"
            implementationClass = "mahjongutils.buildlogic.KmpPlugin"
        }
        register("kmp-compose") {
            id = "mahjongutils.buildlogic.compose"
            implementationClass = "mahjongutils.buildlogic.KmpComposePlugin"
        }
        register("android-lib") {
            id = "mahjongutils.buildlogic.android.lib"
            implementationClass = "mahjongutils.buildlogic.AndroidLibPlugin"
        }
    }
}
