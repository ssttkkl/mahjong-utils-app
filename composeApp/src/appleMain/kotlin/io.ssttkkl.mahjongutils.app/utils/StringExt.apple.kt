package io.ssttkkl.mahjongutils.app.utils

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun String.cformat(vararg args: Any?): String {
    return NSString.stringWithFormat(this, args)
}