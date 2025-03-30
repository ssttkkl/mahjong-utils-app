package io.ssttkkl.mahjongutils.app.base.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ScrollBox(
    verticalScrollState: ScrollState? = null,
    horizontalScrollState: ScrollState? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)

@Composable
expect fun VerticalScrollBox(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)

@Composable
expect fun HorizontalScrollBox(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
