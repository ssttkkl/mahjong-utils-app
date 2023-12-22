package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
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
        if (consumer.consuming)
            return

        coroutineScope = CoroutineScope(Dispatchers.Main).also {
            it.launch {
                tileImeHostState.pendingTile.collect { tile ->
                    state.selection = state.selection.coerceIn(0, currentValue.size)
                    val newValue = buildList {
                        addAll(currentValue.subList(0, state.selection.start))
                        add(tile)

                        if (state.selection.end != currentValue.size) {
                            addAll(
                                currentValue.subList(
                                    state.selection.end + 1,
                                    currentValue.size
                                )
                            )
                        }
                    }
                    currentOnValueChange?.invoke(newValue)
                    state.selection = TextRange(state.selection.start + 1)
                }
            }

            it.launch {
                tileImeHostState.backspace.collect {
                    state.selection = state.selection.coerceIn(0, currentValue.size)
                    val curCursor = state.selection.start
                    if (state.selection.length == 0) {
                        if (curCursor - 1 in currentValue.indices) {
                            val newValue = ArrayList(currentValue).apply {
                                removeAt(curCursor - 1)
                            }
                            currentOnValueChange?.invoke(newValue)
                            state.selection = TextRange(curCursor - 1)
                        }
                    } else {
                        val newValue = buildList {
                            addAll(currentValue.subList(0, state.selection.start))

                            if (state.selection.end != currentValue.size) {
                                addAll(
                                    currentValue.subList(
                                        state.selection.end + 1,
                                        currentValue.size
                                    )
                                )
                            }
                        }
                        currentOnValueChange?.invoke(newValue)
                        state.selection = TextRange(curCursor - 1)
                    }
                }
            }

            it.launch {
                tileImeHostState.collapse.collect {
                    consumer.release()
                }
            }
        }

        consumer.consume()
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