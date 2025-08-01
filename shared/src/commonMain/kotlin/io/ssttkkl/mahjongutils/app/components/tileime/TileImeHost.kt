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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quadible.feather.FloatingDraggableContainer
import com.quadible.feather.FloatingDraggableItem
import com.quadible.feather.FloatingDraggableState
import io.ssttkkl.mahjongutils.app.base.rememberWindowSizeClass
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_drag_handle
import org.jetbrains.compose.resources.painterResource

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

            TileIme(
                state,
                modifier = Modifier.fillMaxWidth()
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
                TileIme(
                    state = state,
                    modifier = Modifier.widthIn(0.dp, 500.dp)
                        .background(MaterialTheme.colorScheme.background),
                    headerContainer = {
                        draggableArea {
                            if (state.pendingText.isEmpty()) {
                                Icon(
                                    painterResource(Res.drawable.icon_drag_handle),
                                    "Move Tile IME",
                                    Modifier.align(Alignment.Center),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            it()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TileImeHost(
    state: TileImeHostState = rememberTileImeHostState(),
    content: @Composable () -> Unit
) {
    val windowSizeClass = rememberWindowSizeClass()

    CompositionLocalProvider(
        LocalTileImeHostState provides state
    ) {
        if (windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
            && windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium
        ) {
            TileImeHostFloating(state, content)
        } else {
            TileImeHostOnBottom(state, content)
        }
    }
}
