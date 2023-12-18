package io.ssttkkl.mahjongutils.app.screens.furoshanten

import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.screens.base.ResultScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import mahjongutils.models.Tile

class FuroShantenScreenModel : ResultScreenModel<FuroChanceShantenCalcResult>() {
    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val chanceTile = MutableStateFlow<Tile?>(null)
    val allowChi = MutableStateFlow(false)

    override suspend fun onCalc(appState: AppState): FuroChanceShantenCalcResult {
        val chanceTile = chanceTile.value!!
        val args = FuroChanceShantenArgs(tiles.value, chanceTile, allowChi.value)
        return args.calc()
    }
}