package io.ssttkkl.mahjongutils.app.utils

actual fun String.cformat(vararg args: Any?): String {
    return String.format(this, *args)
}