package io.ssttkkl.mahjongutils.app.base.utils

import kotlinx.browser.window

actual object PlatformUtils {
    actual val isApple: Boolean
        get() {
            return listOf("macintosh", "mac os x", "iphone", "ipad", "ipod").any {
                window.navigator.userAgent.contains(it, ignoreCase = true)
            }
        }
}