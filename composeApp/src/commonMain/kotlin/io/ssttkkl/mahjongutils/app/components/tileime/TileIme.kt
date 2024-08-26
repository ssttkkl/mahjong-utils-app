package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.backhandler.BackHandler
import io.ssttkkl.mahjongutils.app.components.clickableButNotFocusable
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_copy
import mahjongutils.composeapp.generated.resources.icon_content_paste
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.painterResource

private val tileImeMatrix = listOf(
    Tile.parseTiles("123456789m").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("123456789p").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("123456789s").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("1234567z").map { TileImeKey.TileKey(it) } + TileImeKey.BackspaceKey
)

@Composable
fun TileIme(
    state: TileImeHostState,
    modifier: Modifier = Modifier,
    headerContainer: @Composable (@Composable () -> Unit) -> Unit = { it() },
) {
    val collapsed by remember {
        derivedStateOf { state.specifiedCollapsed ?: state.defaultCollapsed }
    }
    val onLongPress = remember(state) {
        { it: TileImeKey<*> ->
            when (it) {
                is TileImeKey.BackspaceKey -> {
                    state.emitAction(ImeAction.Clear)
                }

                else -> {}
            }
        }
    }
    val onClick = remember(state) {
        { it: TileImeKey<*> ->
            when (it) {
                is TileImeKey.TileKey -> {
                    state.emitAction(ImeAction.Input(listOf(it.tile)))
                }

                is TileImeKey.BackspaceKey -> {
                    state.emitAction(ImeAction.Delete(TileImeHostState.DeleteType.Backspace))
                }
            }
        }
    }

    BackHandler {
        state.specifiedCollapsed = true
    }

    Column(
        modifier
            .background(MaterialTheme.colorScheme.surfaceContainer),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        headerContainer {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    state.pendingText,
                    Modifier.align(Alignment.Center)
                )

                Image(
                    if (!collapsed)
                        Icons.Filled.KeyboardArrowDown
                    else
                        Icons.Filled.KeyboardArrowUp,
                    "",
                    Modifier
                        .padding(start = 8.dp)
                        .clickableButNotFocusable(remember { MutableInteractionSource() }) {
                            state.specifiedCollapsed = !collapsed
                        }
                        .padding(4.dp)
                        .size(24.dp, 24.dp)
                        .align(Alignment.CenterStart),
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )

                Row(Modifier.align(Alignment.CenterEnd)
                    .padding(start = 8.dp)) {
                    Image(
                        painterResource(Res.drawable.icon_content_copy),
                        "",
                        Modifier
                            .padding(start = 8.dp)
                            .clickableButNotFocusable(remember { MutableInteractionSource() }) {
                                state.emitAction(ImeAction.Copy)
                            }
                            .padding(4.dp)
                            .size(24.dp, 24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )

                    Image(
                        painterResource(Res.drawable.icon_content_paste),
                        "",
                        Modifier
                            .padding(start = 8.dp)
                            .let {
                                if (state.clipboardData.isNullOrEmpty()) {
                                    it.alpha(0.4f)
                                } else {
                                    it.clickableButNotFocusable(remember { MutableInteractionSource() }) {
                                        state.emitAction(ImeAction.Paste)
                                    }
                                }
                            }
                            .padding(4.dp)
                            .size(24.dp, 24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }

        if (!collapsed) {
            KeyboardScreen(
                tileImeMatrix,
                onLongPress,
                onClick
            )
        }
    }
}