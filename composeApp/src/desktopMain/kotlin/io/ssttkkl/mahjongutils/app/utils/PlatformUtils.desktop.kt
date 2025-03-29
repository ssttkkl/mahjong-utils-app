package io.ssttkkl.mahjongutils.app.utils

import java.util.Locale

actual object PlatformUtils {
    actual val isApple: Boolean
        get() {
            val os = System.getProperty("os.name").lowercase(Locale.getDefault())
            return os.contains("mac") || os.contains("darwin")
        }
}
