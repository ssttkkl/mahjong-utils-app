package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import mahjongutils.shanten.CommonShantenArgs
import mahjongutils.shanten.CommonShantenArgsErrorInfo
import mahjongutils.shanten.validate
import org.jetbrains.compose.resources.StringResource

class ShantenScreenModel : FormAndResultScreenModel<ShantenArgs, ShantenCalcResult>() {
    var tiles by mutableStateOf<List<Tile>>(emptyList())
    var shantenMode by mutableStateOf(ShantenMode.Union)

    val tilesErrMsg = mutableStateListOf<StringResource>()

    override fun resetForm() {
        tiles = emptyList()
        shantenMode = ShantenMode.Union
        tilesErrMsg.clear()
    }

    override fun onCheck(): Boolean {
        tilesErrMsg.clear()

        val muArgs = CommonShantenArgs(tiles)
        val errors = muArgs.validate()
        for (it in errors) {
            when (it) {
                CommonShantenArgsErrorInfo.tilesIsEmpty -> {
                    tilesErrMsg.add(Res.string.text_must_enter_tiles)
                }

                CommonShantenArgsErrorInfo.tooManyTiles -> {
                    tilesErrMsg.add(Res.string.text_cannot_have_more_than_14_tiles)
                }

                CommonShantenArgsErrorInfo.tilesNumIllegal -> {
                    tilesErrMsg.add(Res.string.text_tiles_must_not_be_divided_into_3)
                }

                CommonShantenArgsErrorInfo.anyTileMoreThan4 -> {
                    tilesErrMsg.add(Res.string.text_any_tile_must_not_be_more_than_4)
                }

                CommonShantenArgsErrorInfo.tooManyFuro -> {}
            }
        }

        return tilesErrMsg.isEmpty()
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