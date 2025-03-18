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

// vercel自带的Java 11，但是AGP要求17，所以添加开关
val Project.enableAndroid
    get() = rootProject.extra.getBoolean("ENABLE_ANDROID")
            && JavaVersion.current() >= JavaVersion.VERSION_17

val Project.enableIos
    get() = rootProject.extra.getBoolean("ENABLE_IOS")
            && System.getProperty("os.name").startsWith("Mac")

val Project.enableDesktop
    get() = rootProject.extra.getBoolean("ENABLE_DESKTOP")

val Project.enableWasm
    get() = rootProject.extra.getBoolean("ENABLE_WASM")
