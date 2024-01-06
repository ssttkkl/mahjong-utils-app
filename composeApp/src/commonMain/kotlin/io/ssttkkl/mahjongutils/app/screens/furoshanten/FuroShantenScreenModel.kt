package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import mahjongutils.models.Tile
import mahjongutils.models.countAsCodeArray

class FuroShantenScreenModel :
    FormAndResultScreenModel<FuroChanceShantenArgs, FuroChanceShantenCalcResult>() {

    var tiles by mutableStateOf<List<Tile>>(emptyList())
    var chanceTile by mutableStateOf<Tile?>(null)
    var allowChi by mutableStateOf(true)

    var tilesErrMsg by mutableStateOf<StringResource?>(null)
    var chanceTileErrMsg by mutableStateOf<StringResource?>(null)

    override fun resetForm() {
        tiles = emptyList()
        chanceTile = null
        allowChi = true

        tilesErrMsg = null
        chanceTileErrMsg = null
    }

    override suspend fun onCheck(): Boolean {
        var validTiles = true
        var validChanceTile = true

        if (tiles.isEmpty()) {
            tilesErrMsg = MR.strings.text_must_enter_tiles
            validTiles = false
        }

        if (chanceTile == null) {
            chanceTileErrMsg = MR.strings.text_must_enter_chance_tile
            validChanceTile = false
        }

        if (validTiles && tiles.size > 14) {
            tilesErrMsg = MR.strings.text_cannot_have_more_than_14_tiles
            validTiles = false
        }

        if (validTiles && tiles.size !in setOf(4, 7, 10, 13)) {
            tilesErrMsg = MR.strings.text_tiles_are_not_without_got
            validTiles = false
        }

        if (validTiles && tiles.countAsCodeArray().any { it > 4 }) {
            tilesErrMsg = MR.strings.text_any_tile_must_not_be_more_than_4
            validTiles = false
        }

        if (validTiles) {
            tilesErrMsg = null
        }
        if (validChanceTile) {
            chanceTileErrMsg = null
        }
        return validTiles && validChanceTile
    }

    override suspend fun onCalc(): FuroChanceShantenCalcResult {
        val chanceTile = chanceTile!!
        val args = FuroChanceShantenArgs(tiles, chanceTile, allowChi)
        screenModelScope.launch(Dispatchers.Default + NonCancellable) {
            FuroChanceShantenArgs.history.insert(args)
        }
        return args.calc()
    }

    override val history: HistoryDataStore<FuroChanceShantenArgs>
        get() = FuroChanceShantenArgs.history
}