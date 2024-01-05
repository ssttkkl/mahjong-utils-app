package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.BackspaceCommand
import androidx.compose.ui.text.input.CommitTextCommand
import androidx.compose.ui.text.input.EditCommand
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.PlatformTextInputService
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputService
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mahjongutils.models.Tile
import kotlin.time.measureTime

class TileImeHostState(
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private val logger = LoggerFactory.getLogger("TileImeHostState")
    }

    var consumer by mutableStateOf(0)
    val pendingTile = MutableSharedFlow<Tile>()
    val backspace = MutableSharedFlow<Unit>()

    var visible by mutableStateOf(false)

    inner class TileImeConsumer {
        var consuming by mutableStateOf(false)
            private set

        private var collectPendingTileJob: Job? = null
        private var collectPendingBackspaceJob: Job? = null

        fun consume(
            handlePendingTile: suspend (tile: Tile) -> Unit,
            handleBackspace: suspend () -> Unit
        ) {
            if (!consuming) {
                consumer += 1
                consuming = true
                visible = true

                collectPendingTileJob = coroutineScope.launch {
                    pendingTile.collect { tile ->
                        handlePendingTile(tile)
                    }
                }

                collectPendingBackspaceJob = coroutineScope.launch {
                    backspace.collect {
                        handleBackspace()
                    }
                }

                logger.debug("start consuming")
            }
        }

        fun release() {
            if (consuming) {
                consumer -= 1
                consuming = false

                collectPendingTileJob?.cancel()
                collectPendingBackspaceJob?.cancel()

                if (consumer == 0) {
                    visible = false
                }

                logger.debug("stop consuming")
            }
        }
    }
}

@Composable
fun TileImeHost(
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = remember { TileImeHostState(scope) }

    val logger = remember { LoggerFactory.getLogger("TileImeHost") }

    LaunchedEffect(state.visible) {
        logger.debug("visible: ${state.visible}")
    }

    CompositionLocalProvider(
        LocalTileImeHostState provides state,
    ) {
        Column {
            Row(Modifier.weight(1f)) {
                content()
            }

            AnimatedVisibility(
                state.visible,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                val tileImeRecompositionTime = measureTime {
                    TileIme(
                        { scope.launch { state.pendingTile.emit(it) } },
                        { scope.launch { state.backspace.emit(Unit) } },
                        { state.visible = false }
                    )
                }
                logger.debug("tileImeRecompositionTime: $tileImeRecompositionTime")
            }
        }
    }
}

@Composable
fun UseTileIme(
    transformTile: (Tile) -> AnnotatedString,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = LocalTileImeHostState.current

    val service = remember(scope, state) {
        var consumer = state.TileImeConsumer()

        val platformService = object : PlatformTextInputService {
            override fun hideSoftwareKeyboard() {
                consumer.release()
            }

            override fun showSoftwareKeyboard() {
                consumer.consume(handlePendingTile = {}, handleBackspace = {})
            }

            override fun startInput(
                value: TextFieldValue,
                imeOptions: ImeOptions,
                onEditCommand: (List<EditCommand>) -> Unit,
                onImeActionPerformed: (ImeAction) -> Unit
            ) {
                consumer.consume(handlePendingTile = {
                    withContext(Dispatchers.Main) {
                        val str = transformTile(it)
                        onEditCommand(listOf(CommitTextCommand(str, str.length)))
                    }
                }, handleBackspace = {
                    withContext(Dispatchers.Main) {
                        onEditCommand(listOf(BackspaceCommand()))
                    }
                })
            }

            override fun stopInput() {
                hideSoftwareKeyboard()
            }

            override fun updateState(oldValue: TextFieldValue?, newValue: TextFieldValue) {

            }
        }

        TextInputService(platformService)
    }

    CompositionLocalProvider(LocalTextInputService provides service) {
        content()
    }
}

val LocalTileImeHostState = compositionLocalOf<TileImeHostState> {
    error("CompositionLocal LocalTileImeHostState not present")
}
