package io.ssttkkl.mahjongutils.app.components.feather

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Stable
class FloatingDraggableState {
    var itemLayoutCoordinates: LayoutCoordinates? by mutableStateOf(null)
    var containerLayoutCoordinates: LayoutCoordinates? by mutableStateOf(null)

    // 如果用户未拖动过(userOffset == null)，则遵循defaultOffset。否则遵循userOffset
    var defaultOffset: IntOffset by mutableStateOf(IntOffset(x = 0, y = 0))
    var userOffset: IntOffset? by mutableStateOf(null)

    val offset: IntOffset
        get() = userOffset ?: defaultOffset

    val itemSize: IntSize
        get() = itemLayoutCoordinates?.size ?: IntSize(0, 0)

    val containerSize: IntSize
        get() = containerLayoutCoordinates?.size ?: IntSize(0, 0)

    val dragAreaSize: IntSize
        get() = IntSize(
            width = containerSize.width - itemSize.width,
            height = containerSize.height - itemSize.height,
        )

}


val LocalFloatingDraggableState = compositionLocalOf<FloatingDraggableState?> { null }