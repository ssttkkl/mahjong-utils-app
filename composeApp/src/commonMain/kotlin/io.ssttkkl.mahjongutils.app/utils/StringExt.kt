package io.ssttkkl.mahjongutils.app.utils

fun String.format(vararg args: Any?): String {
    if (args.isEmpty()) {
        return this
    }

    return replaceFirst("{}", args.first().toString())
        .format(*args.drop(1).toTypedArray())
}