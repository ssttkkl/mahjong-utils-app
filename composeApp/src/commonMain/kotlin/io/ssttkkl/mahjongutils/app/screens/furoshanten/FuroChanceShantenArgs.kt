package io.ssttkkl.mahjongutils.app.screens.furoshanten

import mahjongutils.models.Tile
import mahjongutils.shanten.FuroChanceShantenResult
import mahjongutils.shanten.furoChanceShanten

data class FuroChanceShantenArgs(
    val tiles: List<Tile>,
    val chanceTile: Tile,
    val allowChi: Boolean = true
) {
    fun calc(): FuroChanceShantenCalcResult {
        val result = furoChanceShanten(tiles, chanceTile, allowChi)
        return FuroChanceShantenCalcResult(this, result)
    }
}

data class FuroChanceShantenCalcResult(
    val args: FuroChanceShantenArgs,
    val result: FuroChanceShantenResult
)