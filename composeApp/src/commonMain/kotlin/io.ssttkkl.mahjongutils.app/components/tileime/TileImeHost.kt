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
import io.ssttkkl.mahjongutils.app.LocalAppState
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
    var textFieldValue by mutableStateOf<TextFieldValue?>(null)
    var options by mutableStateOf(ImeOptions.Default)
    val pendingTile = MutableSharedFlow<Tile>()
    val editCommand = MutableSharedFlow<List<EditCommand>>()
    val action = MutableSharedFlow<ImeAction>()
}

@Composable
fun TileImeHost(
    state: TileImeHostState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

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
                { scope.launch { state.editCommand.emit(listOf(BackspaceCommand())) } },
                { scope.launch { state.action.emit(ImeAction.Done) } }
            )
        }
    }
}

@Composable
fun UseTileIme(
    transformTile: (Tile) -> AnnotatedString,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = LocalAppState.current.tileImeHostState

    val service = remember(scope, state) {
        var pendingTileCollector: Job? = null
        var editCommandCollector: Job? = null
        var imeActionCollector: Job? = null

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
                state.options = imeOptions
                showSoftwareKeyboard()
                pendingTileCollector = state.pendingTile.onEach {
                    withContext(Dispatchers.Main) {
                        val str = transformTile(it)
                        state.editCommand.emit(listOf(CommitTextCommand(str, str.length)))
                    }
                }.launchIn(scope)
                editCommandCollector = state.editCommand.onEach {
                    withContext(Dispatchers.Main) {
                        onEditCommand(it)
                    }
                }.launchIn(scope)
                imeActionCollector = state.action.onEach {
                    withContext(Dispatchers.Main) {
                        onImeActionPerformed(it)
                    }
                }.launchIn(scope)
            }

            override fun stopInput() {
                hideSoftwareKeyboard()
                pendingTileCollector?.cancel()
                pendingTileCollector = null
                editCommandCollector?.cancel()
                editCommandCollector = null
                imeActionCollector?.cancel()
                imeActionCollector = null
            }

            override fun updateState(oldValue: TextFieldValue?, newValue: TextFieldValue) {
                state.textFieldValue = newValue
            }
        }

        TextInputService(platformService)
    }

    CompositionLocalProvider(LocalTextInputService provides service) {
        content()
    }
}