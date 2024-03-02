package io.ssttkkl.mahjongutils.app.models.hanhu

import mahjongutils.hanhu.ChildPoint
import mahjongutils.hanhu.ParentPoint

data class HanHuResult(
    val parentPoint: ParentPoint,
    val childPoint: ChildPoint
)