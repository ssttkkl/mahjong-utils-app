package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenCalcResult
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenMode
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_any_tile_must_not_be_more_than_4
import mahjongutils.composeapp.generated.resources.text_cannot_have_more_than_14_tiles
import mahjongutils.composeapp.generated.resources.text_must_enter_tiles
import mahjongutils.composeapp.generated.resources.text_tiles_must_not_be_divided_into_3
import mahjongutils.models.Tile
import mahjongutils.models.countAsCodeArray
import org.jetbrains.compose.resources.StringResource

class ShantenScreenModel : FormAndResultScreenModel<ShantenArgs, ShantenCalcResult>() {
    var tiles by mutableStateOf<List<Tile>>(emptyList())
    var shantenMode by mutableStateOf(ShantenMode.Union)

    var tilesErrMsg by mutableStateOf<StringResource?>(null)

    override fun resetForm() {
        tiles = emptyList()
        shantenMode = ShantenMode.Union
        tilesErrMsg = null
    }

    override suspend fun onCheck(): Boolean {
        if (tiles.isEmpty()) {
            tilesErrMsg = Res.string.text_must_enter_tiles
            return false
        }

        if (tiles.size > 14) {
            tilesErrMsg = Res.string.text_cannot_have_more_than_14_tiles
            return false
        }

        if (tiles.size % 3 == 0) {
            tilesErrMsg = Res.string.text_tiles_must_not_be_divided_into_3
            return false
        }

        if (tiles.countAsCodeArray().any { it > 4 }) {
            tilesErrMsg = Res.string.text_any_tile_must_not_be_more_than_4
            return false
        }

        tilesErrMsg = null
        return true
    }

    override suspend fun onCalc(): ShantenCalcResult {
        val args = ShantenArgs(tiles, shantenMode)
        screenModelScope.launch(Dispatchers.Default + NonCancellable) {
            ShantenArgs.history.insert(args)
        }
        return args.calc()
    }

    override val history: HistoryDataStore<ShantenArgs>
        get() = ShantenArgs.history
}