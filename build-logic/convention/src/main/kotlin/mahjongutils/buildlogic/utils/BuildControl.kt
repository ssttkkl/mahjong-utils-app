package mahjongutils.buildlogic.utils

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.extra

private fun ExtraPropertiesExtension.getBoolean(name: String, default: Boolean = true): Boolean {
    return if (has(name))
        get(name).toString().lowercase().toBooleanStrict()
    else
        default
}

val Project.enableAndroid
    get() = rootProject.extra.getBoolean("ENABLE_ANDROID")

val Project.enableIos
    get() = rootProject.extra.getBoolean("ENABLE_IOS")

val Project.enableDesktop
    get() = rootProject.extra.getBoolean("ENABLE_DESKTOP")

val Project.enableWasm
    get() = rootProject.extra.getBoolean("ENABLE_WASM")
