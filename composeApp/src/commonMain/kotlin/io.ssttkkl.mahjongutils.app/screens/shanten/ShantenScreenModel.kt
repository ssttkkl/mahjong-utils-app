package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

class ShantenScreenModel : ScreenModel {
    val scope = CoroutineScope(Dispatchers.Main)

    override fun onDispose() {
        super.onDispose()
        scope.cancel()
    }

    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val shantenMode = MutableStateFlow(ShantenMode.Union)

    val produceArgs = MutableSharedFlow<ShantenArgs>()

    fun onSubmit(snackbarHostState: SnackbarHostState) {
        val args = ShantenArgs(tiles.value, shantenMode.value)
        scope.launch {
            produceArgs.emit(args)
        }
    }
}