package io.ssttkkl.mahjongutils.app.models.hanhu

import androidx.compose.runtime.Immutable
import mahjongutils.hanhu.ChildPoint
import mahjongutils.hanhu.ParentPoint

@Immutable
data class HanHuResult(
    val parentPoint: ParentPoint,
    val childPoint: ChildPoint
)