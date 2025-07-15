package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import com.quadible.feather.LocalFloatingDraggableState
import io.ssttkkl.mahjongutils.app.base.utils.PlatformUtils
import io.ssttkkl.mahjongutils.app.components.onRightClick
import io.ssttkkl.mahjongutils.app.components.tapPress
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
            state.updateSelection(value.indices) { selection ->
                val newValue = buildList {
                    addAll(value.subList(0, selection.start))
                    addAll(tiles)

                    if (selection.end != value.size) {
                        addAll(
                            value.subList(
                                selection.end,
                                value.size
                            )
                        )
                    }
                }
                onValueChange?.invoke(newValue)
                TextRange(selection.start + tiles.size)
            }
        },
        handleReplaceTile = { tiles ->
            state.updateSelection(value.indices) { _ ->
                onValueChange?.invoke(tiles)
                TextRange(tiles.size)
            }
        },
        handleDeleteTile = {
            state.updateSelection(value.indices) { selection ->
                val curCursor = selection.start
                if (selection.length == 0) {
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
                        TextRange(indexToRemove)
                    } else {
                        selection
                    }
                } else {
                    val newValue = buildList {
                        addAll(value.subList(0, selection.start))

                        if (selection.end != value.size) {
                            addAll(
                                value.subList(
                                    selection.end + 1,
                                    value.size
                                )
                            )
                        }
                    }
                    onValueChange?.invoke(newValue)
                    TextRange(curCursor)
                }
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

private fun Modifier.handleCommandKeyEvent(tileImeHostState: TileImeHostState): Modifier {
    return onKeyEvent {
        if (it.type != KeyEventType.KeyDown) {
            return@onKeyEvent false
        }

        // 注意判断苹果和其他系统
        val isCtrlOrCmdPressed =
            if (PlatformUtils.isApple) it.isMetaPressed else it.isCtrlPressed

        if (it.key == Key.V && isCtrlOrCmdPressed) {
            // 粘贴操作
            tileImeHostState.emitAction(TileImeHostState.ImeAction.Paste)
            true
        } else if (it.key == Key.C && isCtrlOrCmdPressed) {
            // 复制操作
            tileImeHostState.emitAction(TileImeHostState.ImeAction.Copy)
            true
        } else {
            false
        }
    }
}

// 点击时更改键盘的默认折叠
private fun Modifier.onPressDetectTileImeDefaultCollapsed(
    state: TileImeHostState,
): Modifier {
    return pointerInput(Unit) {
        awaitEachGesture {
            val e = awaitFirstDown()
            // 如果是鼠标点击，默认折叠。否则默认展开
            state.defaultCollapsed = e.type == PointerType.Mouse
            // 如果是触摸点击，强制展开键盘
            if (e.type == PointerType.Touch) {
                state.specifiedCollapsed = false
            }
            waitForUpOrCancellation()
        }
    }
}

@Composable
fun BaseTileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyLarge,
    colors: TextFieldColors = TextFieldDefaults.colors(),
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

    var dropdownExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val cursorColor = when {
        isError -> colors.errorCursorColor
        else -> colors.cursorColor
    }

    CoreTileField(
        value = value,
        modifier = modifier.onGloballyPositioned { layoutCoordinates = it }
            .focusRequester(focusRequester)
            .onRightClick(enabled) {
                focusRequester.requestFocus()  // 如果右键时还没有focus，则ime无法绑定到输入框，操作不生效
                dropdownExpanded = true
            }
            .tapPress(
                state.interactionSource,
                enabled,
                onLongPress = {
                    dropdownExpanded = true
                },
                onTap = {
                    focusRequester.requestFocus()
                })
            .onPressDetectTileImeDefaultCollapsed(tileImeHostState)
            .handleCommandKeyEvent(tileImeHostState),
        state = state,
        cursorColor = cursorColor,
        fontSize = fontSize
    )

    TileFieldPopMenu(dropdownExpanded, { dropdownExpanded = !dropdownExpanded })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyMedium,
    label: String? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val shape = TextFieldDefaults.shape
    val colors = TextFieldDefaults.colors()

    val decorationBox = @Composable { innerTextField: @Composable () -> Unit ->
        TextFieldDefaults.DecorationBox(
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
                TextFieldDefaults.ContainerBox(
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
            enabled = enabled,
            fontSize = fontSize,
            colors = colors,
            isError = isError,
            interactionSource = interactionSource
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyMedium,
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
            enabled = enabled,
            fontSize = fontSize,
            colors = colors,
            isError = isError,
            interactionSource = interactionSource
        )
    }
}
