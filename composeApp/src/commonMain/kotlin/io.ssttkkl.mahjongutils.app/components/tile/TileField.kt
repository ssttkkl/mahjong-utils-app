package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

@Composable
fun TileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    isError: Boolean = false,
) {
    val tileImeHostState = LocalTileImeHostState.current

    val coroutineScope = rememberCoroutineScope()
    val coreTileFieldState = remember { CoreTileFieldState() }

    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    DisposableEffect(coreTileFieldState.focused) {
        if (coreTileFieldState.focused) {
            tileImeHostState.visible = true

            val collectPendingTileJob = coroutineScope.launch {
                tileImeHostState.pendingTile.collect { tile ->
                    val newValue = buildList {
                        addAll(currentValue.take(coreTileFieldState.selection.start))
                        add(tile)
                        addAll(currentValue.takeLast(currentValue.size - coreTileFieldState.selection.end))
                    }
                    currentOnValueChange(newValue)
                    coreTileFieldState.selection = TextRange(coreTileFieldState.selection.start + 1)
                }
            }
            val collectBackspaceJob = coroutineScope.launch {
                tileImeHostState.backspace.collect {
                    if (coreTileFieldState.selection.length == 0) {
                        val curCursor = coreTileFieldState.selection.start
                        if (curCursor - 1 in currentValue.indices) {
                            val newValue = ArrayList(currentValue).apply {
                                removeAt(curCursor - 1)
                            }
                            currentOnValueChange(newValue)
                            coreTileFieldState.selection = TextRange(curCursor - 1)
                        }
                    } else {
                        val newValue = buildList {
                            addAll(currentValue.take(coreTileFieldState.selection.start))
                            addAll(currentValue.takeLast(value.size - coreTileFieldState.selection.end))
                        }
                        currentOnValueChange(newValue)
                        coreTileFieldState.selection = TextRange(coreTileFieldState.selection.start)
                    }
                }
            }
            val collectCollapseJob = coroutineScope.launch {
                tileImeHostState.collapse.collect {
                    tileImeHostState.visible = false
                }
            }

            onDispose {
                collectPendingTileJob.cancel()
                collectBackspaceJob.cancel()
                collectCollapseJob.cancel()
            }
        } else {
            onDispose { }
        }
    }

    CoreTileField(
        value = value,
        modifier = modifier,
        state = coreTileFieldState,
        label = label,
        isError = isError
    )
}

//@Composable
//fun TileField(
//    value: List<Tile>,
//    onValueChange: (List<Tile>) -> Unit,
//    modifier: Modifier = Modifier,
//    label: String? = null,
//    textStyle: TextStyle = TilesTextStyle,
//    isError: Boolean = false,
//) {
//    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
//
//    val tilesText = value.joinToString("") { it.emoji }
//    if (tilesText != textFieldValue.text) {
//        // 保证选择的边界是两个emoji之间
//        textFieldValue = TextFieldValue(tilesText)
//    }
//
//    UseTileIme(
//        transformTile = {
//            buildAnnotatedString {
//                append(it.emoji)
//            }
//        }
//    ) {
//        OutlinedTextField(
//            value = textFieldValue,
//            onValueChange = { raw ->
//                // 保证选择的边界在两个emoji之间（每个emoji占两个字符）
//                textFieldValue = raw.let {
//                    it.copy(
//                        selection = TextRange(
//                            max(0, it.selection.start - it.selection.start % 2),
//                            max(0, it.selection.end - it.selection.end % 2)
//                        )
//                    )
//                }
//
//                val tiles = buildList {
//                    repeat(textFieldValue.text.length / 2) {
//                        // 每个tile emoji占两个字符
//                        add(emojiToTile(textFieldValue.text.substring(it * 2, it * 2 + 2)))
//                    }
//                }
//                onValueChange(tiles)
//            },
//            modifier = modifier,
//            textStyle = textStyle,
//            label = {
//                label?.let {
//                    Text(it)
//                }
//            },
//            trailingIcon = {
//                Text(
//                    stringResource(MR.strings.text_tiles_num_short, value.size),
//                    style = MaterialTheme.typography.labelMedium
//                )
//            },
//            isError = isError
//        )
//    }
//}
