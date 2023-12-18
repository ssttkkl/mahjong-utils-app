package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import mahjongutils.models.Tile

class ShantenScreenModel : ScreenModel {
    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val shantenMode = MutableStateFlow(ShantenMode.Union)

    val result = MutableStateFlow<Deferred<ShantenCalcResult>?>(null)

    fun onSubmit(snackbarHostState: SnackbarHostState) {
        val args = ShantenArgs(tiles.value, shantenMode.value)
        result.value = screenModelScope.async(Dispatchers.Default) {
            args.calc()
        }
    }
}