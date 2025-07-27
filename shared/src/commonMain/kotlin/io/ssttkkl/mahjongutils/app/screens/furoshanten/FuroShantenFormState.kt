package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.screens.base.FormState
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

class FuroShantenFormState : FormState<FuroChanceShantenArgs> {
    var tiles by mutableStateOf<List<Tile>>(emptyList())
    var chanceTile by mutableStateOf<Tile?>(null)
    var allowChi by mutableStateOf(true)

    override fun applyFromMap(map: Map<String, String>) {
        map["tiles"]?.let {
            runCatching {
                tiles = Tile.parseTiles(it)
            }
        }
        map["chanceTile"]?.let {
            runCatching {
                chanceTile = Tile.get(it)
            }
        }
        map["allowChi"]?.let {
            runCatching {
                allowChi = it.toBoolean()
            }
        }
    }

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

}