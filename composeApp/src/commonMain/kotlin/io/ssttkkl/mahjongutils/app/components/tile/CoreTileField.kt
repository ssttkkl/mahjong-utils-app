package io.ssttkkl.mahjongutils.app.components.tile


import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.onRightClick
import io.ssttkkl.mahjongutils.app.components.tapPress
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

@Stable
class CoreTileFieldState(
    val interactionSource: MutableInteractionSource
) {
    var selection by mutableStateOf(TextRange.Zero)
}

inline fun CoreTileFieldState.updateSelection(
    coerceIn: IntRange,
    action: (TextRange) -> TextRange
) {
    selection = action(selection.coerceIn(coerceIn.first, coerceIn.last + 1))
}

private fun detectTapPosition(rects: List<Rect>, offset: Offset): Int {
    for (i in rects.indices) {
        val rect = rects[i]
        val middle = (rect.right + rect.left) / 2
        if (offset.x < middle && offset.y <= rect.bottom) {
            return i
        }

        // 点击的是该行的最后一个
        if (i + 1 in rects.indices && rects[i].left > rects[i + 1].left && offset.x > rects[i].left) {
            return i + 1
        }
    }

    return rects.size
}

// 触摸时改变指针位置
private fun Modifier.onTapChangeCursor(
    rects: State<List<Rect>>,
    changeCursorRequest: (Int) -> Unit
): Modifier {
    return pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()
            val up = waitForUpOrCancellation()
            if (up != null) {
                val offset = up.position
                changeCursorRequest(detectTapPosition(rects.value, offset))
            }
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


// 绘制指针
private fun Modifier.drawCursor(
    selection: TextRange,
    rects: State<List<Rect>>,
    color: Color
): Modifier {
    return composed {
        val density = LocalDensity.current

        drawWithContent {
            drawContent()

            val height = rects.value.firstOrNull()?.height ?: size.height
            if (selection.length == 0) {
                val x = if (selection.start < rects.value.size)
                    rects.value[selection.start].left
                else if (rects.value.isNotEmpty())
                    rects.value.last().right
                else
                    0.0f

                val y = if (selection.start < rects.value.size)
                    rects.value[selection.start].top
                else if (rects.value.isNotEmpty())
                    rects.value.last().top
                else
                    0.0f

                drawLine(
                    color,
                    Offset(x, y),
                    Offset(x, y + height),
                    with(density) { 2.dp.toPx() }
                )
            }

        }
    }
}

private val validKeys = mapOf(
    Key.One to "1", Key.Two to "2", Key.Three to "3", Key.Four to "4", Key.Five to "5",
    Key.Six to "6", Key.Seven to "7", Key.Eight to "8", Key.Nine to "9",
    Key.M to "m", Key.P to "p", Key.S to "s", Key.Z to "z"
)

private fun Modifier.handleKeyEvent(tilesCount: Int, state: CoreTileFieldState): Modifier {
    return composed {
        val ime = LocalTileImeHostState.current

        val coroutineScope = rememberCoroutineScope()
        var nowKeyDown by remember { mutableStateOf(false) }

        onKeyEvent {
            if (it.key == Key.Backspace || it.key == Key.Delete) {
                if (it.type == KeyEventType.KeyDown && !nowKeyDown) {
                    // 按下的一瞬间，执行IME或输入框的退格/删除
                    // 长按时可能会收到多个按键事件，如果此前已经有任务则不再处理
                    nowKeyDown = true
                    if (it.key == Key.Backspace) {
                        if (ime.pendingText.isNotEmpty()) {
                            ime.removeLastPendingText(1)
                        } else {
                            ime.emitAction(ImeAction.Delete(TileImeHostState.DeleteType.Backspace))
                        }
                    } else if (it.key == Key.Delete) {
                        if (ime.pendingText.isNotEmpty()) {
                            ime.removeLastPendingText(65535)
                        } else {
                            ime.emitAction(ImeAction.Delete(TileImeHostState.DeleteType.Delete))
                        }
                    }

                    // 延迟500ms后，持续执行光标移动
                    coroutineScope.launch {
                        delay(500)

                        // IME如果有文本，则持续对其退格。且退格到无文本后不再继续对文本框退格
                        if (ime.pendingText.isNotEmpty() && it.key == Key.Backspace) {
                            while (ime.pendingText.isNotEmpty()) {
                                ime.removeLastPendingText(1)
                                delay(100)
                            }
                        } else {
                            while (true) {
                                if (it.key == Key.Backspace) {
                                    ime.emitAction(ImeAction.Delete(TileImeHostState.DeleteType.Backspace))
                                } else {
                                    ime.emitAction(ImeAction.Delete(TileImeHostState.DeleteType.Delete))
                                }
                                delay(100)
                            }
                        }
                    }
                    return@onKeyEvent true
                } else if (it.type == KeyEventType.KeyUp) {
                    coroutineScope.coroutineContext.cancelChildren()
                    nowKeyDown = false
                    return@onKeyEvent true
                }
            }

            if (it.key == Key.DirectionLeft || it.key == Key.DirectionRight) {
                // 按下的一瞬间，执行光标移动
                // 长按时可能会收到多个按键事件，如果此前已经有任务则不再处理
                nowKeyDown = true
                if (it.type == KeyEventType.KeyDown && !nowKeyDown) {
                    if (it.key == Key.DirectionLeft) {
                        if (state.selection.start > 0) {
                            state.selection =
                                TextRange(state.selection.start - 1).coerceIn(0, tilesCount)
                        }
                    } else if (it.key == Key.DirectionRight) {
                        if (state.selection.end < tilesCount) {
                            state.selection =
                                TextRange(state.selection.end + 1).coerceIn(0, tilesCount)
                        }
                    }

                    // 延迟500ms后，持续执行光标移动
                    coroutineScope.launch {
                        delay(500)

                        while (true) {
                            if (it.key == Key.DirectionLeft) {
                                if (state.selection.start > 0) {
                                    state.selection =
                                        TextRange(state.selection.start - 1).coerceIn(
                                            0,
                                            tilesCount
                                        )
                                }
                            } else if (it.key == Key.DirectionRight) {
                                if (state.selection.end < tilesCount) {
                                    state.selection =
                                        TextRange(state.selection.end + 1).coerceIn(
                                            0,
                                            tilesCount
                                        )
                                }
                            }
                            delay(100)
                        }
                    }
                    return@onKeyEvent true
                } else if (it.type == KeyEventType.KeyUp) {
                    coroutineScope.coroutineContext.cancelChildren()
                    nowKeyDown = false
                    return@onKeyEvent true
                }
            }

            if (it.type == KeyEventType.KeyDown && validKeys.containsKey(it.key)) {
                ime.appendPendingText(validKeys[it.key]!!)
                true
            } else {
                false
            }
        }
    }
}

@Composable
internal fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    enabled: Boolean = true,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSize: TextUnit
) {
    val tileImeHostState = LocalTileImeHostState.current
    val focusRequester = remember { FocusRequester() }

    // 记录每个麻将牌相对于Tiles的位置
    var rectsState: MutableState<List<Rect>> = remember { mutableStateOf(emptyList()) }
    val focused by state.interactionSource.collectIsFocusedAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier
            .focusRequester(focusRequester)
            .focusable(enabled, interactionSource = state.interactionSource)
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
            .handleKeyEvent(value.size, state)
            .pointerHoverIcon(PointerIcon.Text)
            .onTapChangeCursor(rectsState) {
                state.selection = TextRange(it)
            }
            .onPressDetectTileImeDefaultCollapsed(tileImeHostState)
            .let {
                if (focused)
                    it.drawCursor(
                        state.selection,
                        rectsState,
                        cursorColor
                    )
                else
                    it
            }
    ) {
        Tiles(
            value,
            fontSize = fontSize,
            onTextLayout = { layoutResult ->
                rectsState.value = layoutResult.multiParagraph.placeholderRects.filterNotNull()
            }
        )
    }
    TileFieldPopMenu(dropdownExpanded, { dropdownExpanded = !dropdownExpanded })
}
