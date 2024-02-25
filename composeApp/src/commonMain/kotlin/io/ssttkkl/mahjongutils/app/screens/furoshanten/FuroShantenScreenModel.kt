package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_any_tile_must_not_be_more_than_4
import mahjongutils.composeapp.generated.resources.text_cannot_have_more_than_14_tiles
import mahjongutils.composeapp.generated.resources.text_must_enter_chance_tile
import mahjongutils.composeapp.generated.resources.text_must_enter_tiles
import mahjongutils.composeapp.generated.resources.text_tiles_are_not_without_got
import mahjongutils.models.Tile
import mahjongutils.shanten.FuroChanceShantenArgsErrorInfo
import mahjongutils.shanten.validate
import org.jetbrains.compose.resources.StringResource

class FuroShantenScreenModel :
    FormAndResultScreenModel<FuroChanceShantenArgs, FuroChanceShantenCalcResult>() {

    var tiles by mutableStateOf<List<Tile>>(emptyList())
    var chanceTile by mutableStateOf<Tile?>(null)
    var allowChi by mutableStateOf(true)

    val tilesErrMsg = mutableStateListOf<StringResource>()
    val chanceTileErrMsg = mutableStateListOf<StringResource>()

    override fun resetForm() {
        tiles = emptyList()
        chanceTile = null
        allowChi = true

        tilesErrMsg.clear()
        chanceTileErrMsg.clear()
    }

    override fun fillFormWithArgs(args: FuroChanceShantenArgs, check: Boolean) {
        tiles = args.tiles
        chanceTile = args.chanceTile
        allowChi = args.allowChi
        if (check) {
            onCheck()
        }
    }

    override fun onCheck(): FuroChanceShantenArgs? {
        tilesErrMsg.clear()
        chanceTileErrMsg.clear()

        // 事前校验
        if (chanceTile == null) {
            chanceTileErrMsg.add(Res.string.text_must_enter_chance_tile)
        }

        // 调库校验
        if (tilesErrMsg.isEmpty() && chanceTileErrMsg.isEmpty()) {
            val muArgs = mahjongutils.shanten.FuroChanceShantenArgs(
                tiles,
                chanceTile!!,
                allowChi
            )
            val errors = muArgs.validate()
            for (it in errors) {
                when (it) {
                    FuroChanceShantenArgsErrorInfo.tilesIsEmpty -> {
                        tilesErrMsg.add(Res.string.text_must_enter_tiles)
                    }

                    FuroChanceShantenArgsErrorInfo.tooManyTiles -> {
                        tilesErrMsg.add(Res.string.text_cannot_have_more_than_14_tiles)
                    }

                    FuroChanceShantenArgsErrorInfo.tilesNumIllegal -> {
                        tilesErrMsg.add(Res.string.text_tiles_are_not_without_got)
                    }

                    FuroChanceShantenArgsErrorInfo.anyTileMoreThan4 -> {
                        tilesErrMsg.add(Res.string.text_any_tile_must_not_be_more_than_4)
                    }
                }
            }
        }

        if (tilesErrMsg.isEmpty() && chanceTileErrMsg.isEmpty()) {
            return FuroChanceShantenArgs(tiles, chanceTile!!, allowChi)
        } else {
            return null
        }
    }

    override suspend fun onCalc(args: FuroChanceShantenArgs): FuroChanceShantenCalcResult {
        screenModelScope.launch(Dispatchers.Default + NonCancellable) {
            FuroChanceShantenArgs.history.insert(args)
        }
        return args.calc()
    }

    override val history: HistoryDataStore<FuroChanceShantenArgs>
        get() = FuroChanceShantenArgs.history
}