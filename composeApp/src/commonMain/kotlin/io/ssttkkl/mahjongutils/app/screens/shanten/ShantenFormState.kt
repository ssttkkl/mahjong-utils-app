package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenMode
import io.ssttkkl.mahjongutils.app.screens.base.FormState
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

class ShantenFormState : FormState<ShantenArgs> {
    var tiles by mutableStateOf<List<Tile>>(emptyList())
    var shantenMode by mutableStateOf(ShantenMode.Union)

    val tilesErrMsg = mutableStateListOf<StringResource>()

    override fun fillFormWithArgs(args: ShantenArgs, check: Boolean) {
        tiles = args.tiles
        shantenMode = args.mode
        if (check) {
            onCheck()
        }
    }

    override fun resetForm() {
        tiles = emptyList()
        shantenMode = ShantenMode.Union
        tilesErrMsg.clear()
    }

    override fun onCheck(): ShantenArgs? {
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

        return if (!tilesErrMsg.isEmpty()) {
            null
        } else {
            ShantenArgs(tiles, shantenMode)
        }
    }
}