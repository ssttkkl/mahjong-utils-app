package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.feather.FloatingDraggableContainer
import io.ssttkkl.mahjongutils.app.components.feather.FloatingDraggableItem
import io.ssttkkl.mahjongutils.app.components.feather.FloatingDraggableState

@Composable
private fun TileImeHostOnBottom(
    state: TileImeHostState,
    content: @Composable () -> Unit
) {
    Column {
        Row(Modifier.weight(1f)) {
            content()
        }

        AnimatedVisibility(
            state.visible,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            val collapsed = state.specifiedCollapsed ?: state.defaultCollapsed

            TileIme(
                pendingText = state.pendingText,
                collapsed = collapsed,
                modifier = Modifier.fillMaxWidth(),
                onCommitTile = { state.emitTile(it) },
                onBackspace = { state.emitBackspaceTile() },
                onChangeCollapsed = { state.specifiedCollapsed = it }
            )
        }
    }
}

@Composable
private fun TileImeHostFloating(
    state: TileImeHostState,
    content: @Composable () -> Unit
) {
    val floatingDraggableState = remember { FloatingDraggableState() }
    FloatingDraggableContainer(floatingDraggableState) {
        content()

        FloatingDraggableItem {
            AnimatedVisibility(
                state.visible,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                val collapsed = state.specifiedCollapsed ?: state.defaultCollapsed

                TileIme(
                    pendingText = state.pendingText,
                    collapsed = collapsed,
                    modifier = Modifier.widthIn(0.dp, 500.dp)
                        .background(MaterialTheme.colorScheme.background),
                    headerContainer = {
                        draggableArea {
                            if (state.pendingText.isEmpty()) {
                                Icon(
                                    Icons.Outlined.Menu,
                                    "",
                                    Modifier.align(Alignment.Center)
                                )
                            }
                            it()
                        }
                    },
                    onCommitTile = { state.emitTile(it) },
                    onBackspace = { state.emitBackspaceTile() },
                    onChangeCollapsed = { state.specifiedCollapsed = it }
                )
            }
        }
    }
}

@Composable
fun TileImeHost(
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = remember { TileImeHostState(scope) }

    CompositionLocalProvider(
        LocalTileImeHostState provides state,
    ) {
        if (LocalAppState.current.windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
            && LocalAppState.current.windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium
        ) {
            TileImeHostFloating(state, content)
        } else {
            TileImeHostOnBottom(state, content)
        }
    }
}

val LocalTileImeHostState = compositionLocalOf<TileImeHostState> {
    error("CompositionLocal LocalTileImeHostState not present")
}
