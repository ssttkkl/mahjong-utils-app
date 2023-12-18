package io.ssttkkl.mahjongutils.app.screens.shanten

import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.screens.base.ResultScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import mahjongutils.models.Tile

class ShantenScreenModel : ResultScreenModel<ShantenCalcResult>() {
    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val shantenMode = MutableStateFlow(ShantenMode.Union)

    override suspend fun onCalc(appState: AppState): ShantenCalcResult {
        val args = ShantenArgs(tiles.value, shantenMode.value)
        return args.calc()
    }
}