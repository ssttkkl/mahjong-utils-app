package io.ssttkkl.mahjongutils.app.utils

actual fun String.cformat(arg: Any?): String {
    return String.format(this, arg)
}