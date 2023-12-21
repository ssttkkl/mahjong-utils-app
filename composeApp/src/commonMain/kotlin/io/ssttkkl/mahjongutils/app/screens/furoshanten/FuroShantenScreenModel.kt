package io.ssttkkl.mahjongutils.app.screens.furoshanten

import cafe.adriel.voyager.core.model.screenModelScope
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenCalcResult
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.screens.base.ResultScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mahjongutils.models.Tile
import mahjongutils.models.countAsCodeArray

class FuroShantenScreenModel : ResultScreenModel<FuroChanceShantenCalcResult>() {
    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val chanceTile = MutableStateFlow<Tile?>(null)
    val allowChi = MutableStateFlow(true)

    val tilesErrMsg = MutableStateFlow<StringResource?>(null)
    val chanceTileErrMsg = MutableStateFlow<StringResource?>(null)

    override suspend fun onCheck(): Boolean {
        val tiles = tiles.value
        val chanceTile = chanceTile.value

        var validTiles = true
        var validChanceTile = true

        if (tiles.size == 0) {
            tilesErrMsg.value = MR.strings.text_must_enter_tiles
            validTiles = false
        }

        if (chanceTile == null) {
            chanceTileErrMsg.value = MR.strings.text_must_enter_chance_tile
            validChanceTile = false
        }

        if (validTiles && tiles.size > 14) {
            tilesErrMsg.value = MR.strings.text_cannot_have_more_than_14_tiles
            validTiles = false
        }

        if (validTiles && tiles.size !in setOf(4, 7, 10, 13)) {
            tilesErrMsg.value = MR.strings.text_tiles_are_not_without_got
            validTiles = false
        }

        if (validTiles && tiles.countAsCodeArray().any { it > 4 }) {
            tilesErrMsg.value = MR.strings.text_any_tile_must_not_be_more_than_4
            validTiles = false
        }

        if (validTiles) {
            tilesErrMsg.value = null
        }
        if (validChanceTile) {
            chanceTileErrMsg.value = null
        }
        return validTiles && validChanceTile
    }

    override suspend fun onCalc(appState: AppState): FuroChanceShantenCalcResult {
        val chanceTile = chanceTile.value!!
        val args = FuroChanceShantenArgs(tiles.value, chanceTile, allowChi.value)
        screenModelScope.launch(Dispatchers.Default + NonCancellable) {
            FuroChanceShantenArgs.history.insert(args)
        }
        return args.calc()
    }
}