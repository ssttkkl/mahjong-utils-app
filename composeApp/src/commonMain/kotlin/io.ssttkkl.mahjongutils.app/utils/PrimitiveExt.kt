package io.ssttkkl.mahjongutils.app.utils

fun Double.percentile(precision: Int = 2): String {
//    val scale = 10f.pow(precision)
//    return ((this * 100 * scale).toInt().toDouble() / scale).toString()
    return "%.${precision}f".cformat(this * 100)
}