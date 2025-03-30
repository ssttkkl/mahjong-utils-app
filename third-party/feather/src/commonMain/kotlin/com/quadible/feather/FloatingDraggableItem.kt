package com.quadible.feather

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

interface FloatingDraggableItemScope : BoxScope {
    @Composable
    fun draggableArea(content: @Composable BoxScope.() -> Unit)
}

@Composable
fun FloatingDraggableItem(
    content: @Composable FloatingDraggableItemScope.() -> Unit,
) {
    val state = LocalFloatingDraggableState.current
        ?: error("You must put FloatingDraggableItem inside FloatingDraggableContainer")

    Box(
        modifier = Modifier
            .offset { state.offset }
            .onGloballyPositioned {
                state.itemLayoutCoordinates = it
            },
    ) {
        val scope = object : FloatingDraggableItemScope, BoxScope by this {
            @Composable
            override fun draggableArea(content: @Composable BoxScope.() -> Unit) {
                Box(
                    Modifier.pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            val calculatedX = state.offset.x + dragAmount.x.roundToInt()
                            val calculatedY = state.offset.y + dragAmount.y.roundToInt()

                            val offset = IntOffset(
                                calculatedX.coerceIn(0, state.dragAreaSize.width),
                                calculatedY.coerceIn(0, state.dragAreaSize.height),
                            )
                            state.userOffset = offset
                        }
                    }
                ) {
                    content()
                }
            }
        }
        content(scope)
    }
}