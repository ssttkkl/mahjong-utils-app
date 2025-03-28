package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.backhandler.BackHandler
import io.ssttkkl.mahjongutils.app.components.clickableButNotFocusable
import io.ssttkkl.mahjongutils.app.components.tile.TileImage
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_copy
import mahjongutils.composeapp.generated.resources.icon_content_paste
import mahjongutils.composeapp.generated.resources.label_clear
import mahjongutils.composeapp.generated.resources.label_copy
import mahjongutils.composeapp.generated.resources.label_paste
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                    Modifier.align(Alignment.Center),
                    MaterialTheme.colorScheme.onSurface
                )

                Image(
                    if (!collapsed)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
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

                TilePopMenu(
                    state,
                    Modifier.align(Alignment.CenterEnd)
                        .padding(start = 8.dp)
                )
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

@Composable
private fun TilePopMenu(state: TileImeHostState, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        // 触发按钮
        Image(
            Icons.Default.MoreVert,
            "",
            Modifier
                .padding(start = 8.dp)
                .clickableButNotFocusable(remember { MutableInteractionSource() }) {
                    expanded = true
                }
                .padding(4.dp)
                .size(24.dp, 24.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )

        // 下拉菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Row(Modifier.padding(vertical = 8.dp)) {
                        Icon(painterResource(Res.drawable.icon_content_copy), "")
                        Text(
                            stringResource(Res.string.label_copy),
                            Modifier.padding(horizontal = 8.dp)
                        )
                    }
                },
                onClick = {
                    state.emitAction(ImeAction.Copy)
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Icon(painterResource(Res.drawable.icon_content_paste), "")
                            Text(
                                stringResource(Res.string.label_paste),
                                Modifier.padding(horizontal = 8.dp)
                            )
                        }
                        state.clipboardData?.let { tiles ->
                            Spacer(Modifier.height(8.dp))
                            Row {
                                tiles.forEach {
                                    TileImage(it, Modifier.height(24.dp))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                },
                onClick = {
                    state.emitAction(ImeAction.Paste)
                    expanded = false
                },
                enabled = !state.clipboardData.isNullOrEmpty()
            )

            DropdownMenuItem(
                text = {
                    Row(Modifier.padding(vertical = 8.dp)) {
                        Icon(Icons.Default.Clear, "")
                        Text(
                            stringResource(Res.string.label_clear),
                            Modifier.padding(horizontal = 8.dp)
                        )
                    }
                },
                onClick = {
                    state.emitAction(ImeAction.Clear)
                    expanded = false
                }
            )
        }
    }
}