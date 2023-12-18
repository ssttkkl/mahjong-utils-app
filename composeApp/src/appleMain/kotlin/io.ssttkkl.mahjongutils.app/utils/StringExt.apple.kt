package io.ssttkkl.mahjongutils.app.utils

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun String.cformat(arg: Any?): String {
    return NSString.stringWithFormat(this, arg)
}