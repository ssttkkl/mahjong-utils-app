package io.ssttkkl.mahjongutils.app.components.resultdisplay

import mahjongutils.shanten.ShantenWithoutGot

/** A percentage is undefined when there are no available advance tiles. */
internal fun ShantenWithoutGot.goodShapeAdvanceRatio(): Double? =
    if (advanceNum > 0) 1.0 * (goodShapeAdvanceNum ?: 0) / advanceNum else null
