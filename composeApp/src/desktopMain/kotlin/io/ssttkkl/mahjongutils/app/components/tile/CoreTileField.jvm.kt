package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ssttkkl.mahjongutils.app.components.tapPress
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import mahjongutils.models.Tile

private fun detectTapPosition(rects: List<Rect>, offset: Offset): Int {
    for (i in rects.indices) {
        val rect = rects[i]
        val middle = (rect.right + rect.left) / 2
        if (offset.x < middle) {
            return i
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

            val height = size.height
            if (selection.length == 0) {
                val x = if (selection.start < rects.value.size)
                    rects.value[selection.start].left
                else if (rects.value.isNotEmpty())
                    rects.value.last().right
                else
                    0.0f
                drawLine(
                    color,
                    Offset(x, 0.0f),
                    Offset(x, height),
                    with(density) { 2.dp.toPx() }
                )
            }

        }
    }
}

private val validKeys = mapOf(
    Key.One to "1", Key.Two to "2", Key.Three to "3", Key.Four to "4", Key.Five to "5",
    Key.Six to "6", Key.Seven to "7", Key.Eight to "8", Key.Nine to "9", Key.Zero to "0",
    Key.M to "m", Key.P to "p", Key.S to "s", Key.Z to "z"
)

private fun Modifier.handleKeyEvent(tilesCount: Int, state: CoreTileFieldState): Modifier {
    return composed {
        val ime = LocalTileImeHostState.current

        onKeyEvent {
            if (it.type != KeyEventType.KeyUp) {
                return@onKeyEvent false
            }

            if (it.key == Key.Backspace) {
                if (ime.pendingText.isNotEmpty()) {
                    ime.emitBackspacePendingText(1)
                } else {
                    ime.emitBackspaceTile()
                }
                true
            } else if (it.key == Key.Delete) {
                if (ime.pendingText.isNotEmpty()) {
                    ime.emitBackspacePendingText(1)
                } else {
                    ime.emitDeleteTile()
                }
                true
            } else if (it.key == Key.DirectionLeft) {
                state.selection = TextRange(state.selection.start - 1).coerceIn(0, tilesCount)
                true
            } else if (it.key == Key.DirectionRight) {
                state.selection = TextRange(state.selection.end + 1).coerceIn(0, tilesCount)
                true
            } else if (validKeys.containsKey(it.key)) {
                ime.appendPendingText(validKeys[it.key]!!)
                true
            } else {
                false
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
    placeholder: String?,
) {
    val focusRequester = remember { FocusRequester() }

    // 记录每个麻将牌相对于Tiles的位置
    var rectsState: MutableState<List<Rect>> = remember { mutableStateOf(emptyList()) }
    val focused by state.interactionSource.collectIsFocusedAsState()

    FlowRow(
        modifier = modifier.focusRequester(focusRequester)
            .focusable(interactionSource = state.interactionSource)
            .tapPress(state.interactionSource) {
                focusRequester.requestFocus()
            }
            .handleKeyEvent(value.size, state)
    ) {
        Tiles(
            value,
            modifier
                .onTapChangeCursor(rectsState) {
                    state.selection = TextRange(it)
                }
                .let {
                    if (focused)
                        it.drawCursor(
                            state.selection,
                            rectsState,
                            cursorColor
                        )
                    else
                        it
                },
            fontSize = fontSizeInSp.sp,
            onTextLayout = { layoutResult ->
                rectsState.value = layoutResult.multiParagraph.placeholderRects.filterNotNull()
            }
        )
    }
}
