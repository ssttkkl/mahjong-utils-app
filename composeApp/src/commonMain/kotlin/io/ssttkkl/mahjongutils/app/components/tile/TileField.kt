package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import io.ssttkkl.mahjongutils.app.components.feather.LocalFloatingDraggableState
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_tiles_num_short
import mahjongutils.models.Tile
import mahjongutils.models.toTilesString
import org.jetbrains.compose.resources.stringResource

private fun TileImeHostState.TileImeConsumer.consume(
    state: CoreTileFieldState,
    valueState: State<List<Tile>>,
    onValueChangeState: State<((List<Tile>) -> Unit)?>
) {
    val value by valueState
    val onValueChange by onValueChangeState

    this.consume(
        handlePendingTile = { tiles ->
            state.selection = state.selection.coerceIn(0, value.size)
            val newValue = buildList {
                addAll(value.subList(0, state.selection.start))
                addAll(tiles)

                if (state.selection.end != value.size) {
                    addAll(
                        value.subList(
                            state.selection.end,
                            value.size
                        )
                    )
                }
            }
            onValueChange?.invoke(newValue)
            state.selection = TextRange(state.selection.start + tiles.size)
        },
        handleDeleteTile = {
            state.selection = state.selection.coerceIn(0, value.size)
            val curCursor = state.selection.start
            if (state.selection.length == 0) {
                val indexToRemove = if (it == TileImeHostState.DeleteType.Backspace) {
                    curCursor - 1
                } else {
                    curCursor
                }

                if (indexToRemove in value.indices) {
                    val newValue = ArrayList(value).apply {
                        removeAt(indexToRemove)
                    }
                    onValueChange?.invoke(newValue)
                    state.selection = TextRange(indexToRemove)
                }
            } else {
                val newValue = buildList {
                    addAll(value.subList(0, state.selection.start))

                    if (state.selection.end != value.size) {
                        addAll(
                            value.subList(
                                state.selection.end + 1,
                                value.size
                            )
                        )
                    }
                }
                onValueChange?.invoke(newValue)
                state.selection = TextRange(curCursor)
            }
        },
        handleCopyRequest = {
            value
        },
        handleClearRequest = {
            onValueChange?.invoke(emptyList())
        }
    )
}

@Composable
fun BaseTileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyLarge,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource,
) {
    val state = remember(interactionSource) {
        CoreTileFieldState(interactionSource)
    }

    val currentValueState = rememberUpdatedState(value)
    val currentOnValueChangeState = rememberUpdatedState(onValueChange)

    val tileImeHostState = LocalTileImeHostState.current
    val consumer = remember(state, tileImeHostState) {
        tileImeHostState.TileImeConsumer()
    }

    LaunchedEffect(value) {
        // 限制selection在值范围内
        state.selection = state.selection.coerceIn(0, value.size)
    }

    // 退出时隐藏键盘
    DisposableEffect(consumer) {
        onDispose {
            consumer.release()
        }
    }

    // 绑定键盘到该输入框
    val focused by interactionSource.collectIsFocusedAsState()
    DisposableEffect(enabled && focused, consumer) {
        if (enabled && focused) {
            consumer.consume(state, currentValueState, currentOnValueChangeState)

            onDispose {
                consumer.release()
            }
        } else {
            consumer.release()
            onDispose { }
        }
    }

    // 设置键盘浮窗的位置
    val draggableState = LocalFloatingDraggableState.current
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    var wasOffsetSet by remember { mutableStateOf(false) }
    LaunchedEffect(focused, layoutCoordinates, draggableState, wasOffsetSet) {
        if (focused && !wasOffsetSet
            && layoutCoordinates != null
            && draggableState?.containerLayoutCoordinates != null
        ) {
            val fieldBounds = layoutCoordinates!!.boundsInWindow()
            val containerBounds = draggableState.containerLayoutCoordinates!!.boundsInWindow()
            draggableState.defaultOffset = IntOffset(
                (fieldBounds.left - containerBounds.left).toInt(),
                (fieldBounds.bottom - containerBounds.top).toInt(),
            )
            wasOffsetSet = true
        }
    }
    LaunchedEffect(focused) {
        if (!focused) {
            wasOffsetSet = false
        }
    }

    val cursorColor: Color = MaterialTheme.colorScheme.primary
    val errorCursorColor: Color = MaterialTheme.colorScheme.error

    CoreTileField(
        value = value,
        modifier = modifier.onGloballyPositioned {
            layoutCoordinates = it
        }.onKeyEvent {
            if (it.type != KeyEventType.KeyUp) {
                return@onKeyEvent false
            }

            //TODO: mac/ios上cmd+v会不会响应
            if (it.key == Key.V && it.isCtrlPressed) {
                // 粘贴操作
                tileImeHostState.emitAction(TileImeHostState.ImeAction.Paste)
                true
            } else if (it.key == Key.C && it.isCtrlPressed) {
                // 复制操作
                tileImeHostState.emitAction(TileImeHostState.ImeAction.Copy)
                true
            } else {
                false
            }
        },
        state = state,
        cursorColor = if (isError) errorCursorColor else cursorColor,
        fontSizeInSp = if (fontSize.isSp)
            fontSize.value
        else
            LocalTextStyle.current.fontSize.value,
        placeholder = label
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyLarge,
    label: String? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val shape = OutlinedTextFieldDefaults.shape
    val colors = OutlinedTextFieldDefaults.colors()

    val decorationBox = @Composable { innerTextField: @Composable () -> Unit ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value.toTilesString(),
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label?.let { ({ Text(label) }) },
            trailingIcon = {
                Text(
                    stringResource(Res.string.text_tiles_num_short, value.size),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            supportingText = supportingText,
            singleLine = true,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled,
                    isError,
                    interactionSource,
                    colors,
                    shape
                )
            }
        )
    }
    decorationBox {
        BaseTileField(
            value,
            onValueChange,
            modifier,
            label = label,
            enabled = enabled,
            fontSize = fontSize,
            isError = isError,
            interactionSource = interactionSource
        )
    }
}
