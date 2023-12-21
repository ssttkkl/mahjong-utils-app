package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

class CoreTileFieldState(
    val interactionSource: MutableInteractionSource
) {
    var selection by mutableStateOf(TextRange.Zero)
}

class TileFieldImeConsumer(
    val state: CoreTileFieldState,
    val tileImeHostState: TileImeHostState,
) {
    private val consumer = tileImeHostState.TileImeConsumer()
    private var coroutineScope: CoroutineScope? = null

    var currentValue: List<Tile> = emptyList()
    var currentOnValueChange: ((List<Tile>) -> Unit)? = null

    fun start() {
        coroutineScope = CoroutineScope(Dispatchers.Main).also {
            it.launch {
                tileImeHostState.pendingTile.collect { tile ->
                    val newValue = buildList {
                        addAll(currentValue.take(state.selection.start))
                        add(tile)

                        val restToTake = currentValue.size - state.selection.end
                        if (restToTake > 0) {
                            addAll(currentValue.takeLast(currentValue.size - state.selection.end))
                        }
                    }
                    currentOnValueChange?.invoke(newValue)
                    state.selection = TextRange(state.selection.start + 1)
                }
            }

            it.launch {
                tileImeHostState.backspace.collect {
                    if (state.selection.length == 0) {
                        val curCursor = state.selection.start
                        if (curCursor - 1 in currentValue.indices) {
                            val newValue = ArrayList(currentValue).apply {
                                removeAt(curCursor - 1)
                            }
                            currentOnValueChange?.invoke(newValue)
                            state.selection = TextRange(curCursor - 1)
                        }
                    } else {
                        val newValue = buildList {
                            addAll(currentValue.take(state.selection.start))
                            addAll(currentValue.takeLast(currentValue.size - state.selection.end))
                        }
                        currentOnValueChange?.invoke(newValue)
                        state.selection = TextRange(state.selection.start)
                    }
                }
            }

            it.launch {
                tileImeHostState.collapse.collect {
                    consumer.release()
                }
            }

            consumer.consume()
        }
    }

    fun stop() {
        coroutineScope?.cancel()
        coroutineScope = null

        consumer.release()
    }
}

@Composable
internal expect fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
)