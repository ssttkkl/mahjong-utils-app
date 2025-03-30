package io.ssttkkl.mahjongutils.app.base.utils

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.percentile(precision: Int = 2): String {
    val scale = 10f.pow(precision)
    val scaledNum = (this * 100 * scale).roundToInt()
    val percentage = scaledNum.toDouble() / scale
    return percentage.toString()
//    return "%.${precision}f".cformat(this * 100)
}