package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import com.quadible.feather.LocalFloatingDraggableState
import io.ssttkkl.mahjongutils.app.base.utils.PlatformUtils
import io.ssttkkl.mahjongutils.app.components.onRightClick
import io.ssttkkl.mahjongutils.app.components.tapPress
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import io.ssttkkl.mahjongutils.app.base.utils.TileTextSize
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_tiles_num_short
import mahjongutils.models.Tile
import mahjongutils.models.toTilesString
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt


private fun Modifier.handleShortcutKeyEvent(
    tileRecognizer: TileRecognizer,
    clipboard: Clipboard,
    onAction: (TileImeHostState.ImeAction) -> Unit
): Modifier {
    return onKeyEvent {
        if (it.type != KeyEventType.KeyDown) {
            return@onKeyEvent false
        }

        // 注意判断苹果和其他系统
        val isCtrlOrCmdPressed =
            if (PlatformUtils.isApple) it.isMetaPressed else it.isCtrlPressed

        if (it.key == Key.V && isCtrlOrCmdPressed) {
            tileRecognizer.coroutineScope.launch {
                // 粘贴操作
                // 先尝试从剪切板识别图片
                val bitmap = tileRecognizer.readClipboardImage(clipboard)
                if (bitmap != null) {
                    tileRecognizer.cropAndRecognizeAndFillFromBitmap(bitmap, onAction)
                } else {
                    onAction(TileImeHostState.ImeAction.Paste)
                }
            }
            true
        } else if (it.key == Key.C && isCtrlOrCmdPressed) {
            // 复制操作
            onAction(TileImeHostState.ImeAction.Copy)
            true
        } else {
            false
        }
    }
}

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
private fun BaseTileField(
    valueState: MutableState<List<Tile>>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyLarge,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    isError: Boolean = false,
    interactionSource: MutableInteractionSource,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() }
) {
    val clipboard = LocalClipboard.current
    val state = remember(interactionSource) {
        CoreTileFieldState(
            valueState = valueState,
            interactionSource = interactionSource,
            clipboard = clipboard
        )
    }

    val coroutineScope = rememberCoroutineScope()

    val tileImeHostState = LocalTileImeHostState.current
    val consumer = remember(state, tileImeHostState) {
        tileImeHostState.TileImeConsumer()
    }

    LaunchedEffect(valueState.value) {
        // 限制selection在值范围内
        state.selection = state.selection.coerceIn(0, valueState.value.size)
    }

    // 退出时隐藏键盘
    DisposableEffect(consumer) {
        onDispose {
            consumer.release()
        }
    }

    // 绑定键盘到该输入框
    val focused by interactionSource.collectIsFocusedAsState()
    LaunchedEffect(enabled && focused) {
        if (enabled && focused) {
            consumer.consume(state::handleImeAction)
        } else {
            consumer.release()
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

    val focusRequester = remember { FocusRequester() }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var popupPosition by remember { mutableStateOf(IntOffset.Zero) }
    val density = LocalDensity.current

    val cursorColor = when {
        isError -> colors.errorCursorColor
        else -> colors.cursorColor
    }

    val tileRecognizer = LocalTileRecognizer.current

    Box(
        modifier.onGloballyPositioned { layoutCoordinates = it }
            .semantics {
                // 使组件在视图树中显示为可点击
                onClick(action = null)
            }
    ) {
        decorationBox {
            CoreTileField(
                modifier = modifier.onGloballyPositioned { layoutCoordinates = it }
                    .focusRequester(focusRequester)
                    // 右键展开下拉框
                    .onRightClick(enabled) { position ->
                        focusRequester.requestFocus()
                        popupPosition = IntOffset(
                            position.x.roundToInt(),
                            position.y.roundToInt()
                        )
                        dropdownExpanded = true
                    }
                    // 点击时获取焦点，长按展开下拉框
                    .tapPress(
                        state.interactionSource,
                        enabled,
                        onLongPress = { position ->
                            focusRequester.requestFocus()
                            popupPosition = IntOffset(
                                position.x.roundToInt(),
                                position.y.roundToInt()
                            )
                            dropdownExpanded = true
                        },
                        onTap = {
                            focusRequester.requestFocus()
                        })
                    // 点击时更改键盘的默认折叠
                    .onPressDetectTileImeDefaultCollapsed(tileImeHostState)
                    // 处理复制粘贴快捷键
                    .handleShortcutKeyEvent(tileRecognizer, clipboard, tileImeHostState::emitAction),
                state = state,
                cursorColor = cursorColor,
                fontSize = fontSize
            )

            TileFieldPopMenu(
                expanded = dropdownExpanded,
                onAction = {
                    coroutineScope.launch { state.handleImeAction(it) }
                },
                onDismissRequest = { dropdownExpanded = false },
                offset = with(density) {
                    DpOffset(popupPosition.x.toDp(), popupPosition.y.toDp())
                }
            )
        }
    }
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
    val valueState = remember { mutableStateOf(value) }
    LaunchedEffect(value) { valueState.value = value }
    LaunchedEffect(valueState.value) { onValueChange(valueState.value) }

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
    BaseTileField(
        valueState,
        modifier,
        enabled = enabled,
        fontSize = fontSize,
        colors = colors,
        isError = isError,
        interactionSource = interactionSource,
        decorationBox = decorationBox
    )
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
    val valueState = remember { mutableStateOf(value) }
    LaunchedEffect(value) { valueState.value = value }
    LaunchedEffect(valueState.value) { onValueChange(valueState.value) }

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
    BaseTileField(
        valueState,
        modifier,
        enabled = enabled,
        fontSize = fontSize,
        colors = colors,
        isError = isError,
        interactionSource = interactionSource,
        decorationBox = decorationBox
    )
}
