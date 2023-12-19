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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mahjongutils.models.Tile

class TileImeHostState {
    var visible by mutableStateOf(false)
    val pendingTile = MutableSharedFlow<Tile>()
    val backspace = MutableSharedFlow<Unit>()
    val collapse = MutableSharedFlow<Unit>()
}

@Composable
fun TileImeHost(
    content: @Composable () -> Unit
) {
    val state = remember { TileImeHostState() }
    val scope = rememberCoroutineScope()

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
                TileIme(
                    { scope.launch { state.pendingTile.emit(it) } },
                    { scope.launch { state.backspace.emit(Unit) } },
                    { scope.launch { state.collapse.emit(Unit) } }
                )
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
        var pendingTileCollector: Job? = null
        var backspaceCollector: Job? = null
        var collapseCollector: Job? = null

        val platformService = object : PlatformTextInputService {
            override fun hideSoftwareKeyboard() {
                state.visible = false
            }

            override fun showSoftwareKeyboard() {
                state.visible = true
            }

            override fun startInput(
                value: TextFieldValue,
                imeOptions: ImeOptions,
                onEditCommand: (List<EditCommand>) -> Unit,
                onImeActionPerformed: (ImeAction) -> Unit
            ) {
                showSoftwareKeyboard()
                pendingTileCollector = state.pendingTile.onEach {
                    withContext(Dispatchers.Main) {
                        val str = transformTile(it)
                        onEditCommand(listOf(CommitTextCommand(str, str.length)))
                    }
                }.launchIn(scope)
                backspaceCollector = state.backspace.onEach {
                    withContext(Dispatchers.Main) {
                        onEditCommand(listOf(BackspaceCommand()))
                    }
                }.launchIn(scope)
                collapseCollector = state.collapse.onEach {
                    withContext(Dispatchers.Main) {
                        onImeActionPerformed(ImeAction.Done)
                    }
                }.launchIn(scope)
            }

            override fun stopInput() {
                hideSoftwareKeyboard()
                pendingTileCollector?.cancel()
                pendingTileCollector = null
                backspaceCollector?.cancel()
                backspaceCollector = null
                collapseCollector?.cancel()
                collapseCollector = null
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
