package io.ssttkkl.mahjongutils.app.base.components.feather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun FloatingDraggableContainer(
    state: FloatingDraggableState = remember { FloatingDraggableState() },
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(FloatingDraggableState) -> Unit
) {
    CompositionLocalProvider(LocalFloatingDraggableState provides state) {
        Box(
            modifier.onGloballyPositioned { state.containerLayoutCoordinates = it },
        ) {
            content(state)
        }
    }
}