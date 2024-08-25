package io.ssttkkl.mahjongutils.app.components.scrollbox

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ScrollBox(
    verticalScrollState: ScrollState?,
    horizontalScrollState: ScrollState?,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // ignored, only desktop needs scroll bar
    Box(modifier) {
        content()
    }
}

@Composable
actual fun VerticalScrollBox(
    lazyListState: LazyListState,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // ignored, only desktop needs scroll bar
    Box(modifier) {
        content()
    }
}

@Composable
actual fun HorizontalScrollBox(
    lazyListState: LazyListState,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // ignored, only desktop needs scroll bar
    Box(modifier) {
        content()
    }
}
