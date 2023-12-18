package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

class FuroShantenScreenModel : ScreenModel {
    val scope = CoroutineScope(Dispatchers.Main)

    override fun onDispose() {
        super.onDispose()
        scope.cancel()
    }

    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val chanceTile = MutableStateFlow<Tile?>(null)
    val allowChi = MutableStateFlow(false)

    val produceArgs = MutableSharedFlow<FuroChanceShantenArgs>()

    fun onSubmit(snackbarHostState: SnackbarHostState) {
        val chanceTile = chanceTile.value ?: return
        val args = FuroChanceShantenArgs(tiles.value, chanceTile, allowChi.value)
        scope.launch {
            produceArgs.emit(args)
        }
    }
}