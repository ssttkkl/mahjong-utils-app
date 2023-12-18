package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
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

    val result = MutableStateFlow<Deferred<FuroChanceShantenCalcResult>?>(null)

    fun onSubmit(snackbarHostState: SnackbarHostState) {
        val chanceTile = chanceTile.value ?: return
        val args = FuroChanceShantenArgs(tiles.value, chanceTile, allowChi.value)
        result.value = scope.async(Dispatchers.Default) {
            args.calc()
        }
    }
}