package io.ssttkkl.mahjongutils.app.screens.shanten

import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.screens.base.ResultScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import mahjongutils.models.Tile
import mahjongutils.models.countAsCodeArray

class ShantenScreenModel : ResultScreenModel<ShantenCalcResult>() {
    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val shantenMode = MutableStateFlow(ShantenMode.Union)

    val tilesErrMsg = MutableStateFlow<StringResource?>(null)

    override suspend fun onCheck(): Boolean {
        val tiles = tiles.value

        if (tiles.size == 0) {
            tilesErrMsg.value = MR.strings.text_must_enter_tiles
            return false
        }

        if (tiles.size > 14) {
            tilesErrMsg.value = MR.strings.text_cannot_have_more_than_14_tiles
            return false
        }

        if (tiles.size % 3 == 0) {
            tilesErrMsg.value = MR.strings.text_tiles_must_not_be_divided_into_3
            return false
        }

        if (tiles.countAsCodeArray().any { it > 4 }) {
            tilesErrMsg.value = MR.strings.text_any_tile_must_not_be_more_than_4
            return false
        }

        tilesErrMsg.value = null
        return true
    }

    override suspend fun onCalc(appState: AppState): ShantenCalcResult {
        val args = ShantenArgs(tiles.value, shantenMode.value)
        return args.calc()
    }
}