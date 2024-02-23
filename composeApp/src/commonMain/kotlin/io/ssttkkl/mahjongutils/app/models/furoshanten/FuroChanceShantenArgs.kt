package io.ssttkkl.mahjongutils.app.models.furoshanten

import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.serialization.Serializable
import mahjongutils.models.Tile
import mahjongutils.shanten.FuroChanceShantenResult
import mahjongutils.shanten.furoChanceShanten
import kotlin.reflect.typeOf

@Serializable
data class FuroChanceShantenArgs(
    val tiles: List<Tile>,
    val chanceTile: Tile,
    val allowChi: Boolean = true
) {
    fun calc(): FuroChanceShantenCalcResult {
        val result = furoChanceShanten(tiles, chanceTile, allowChi)
        return FuroChanceShantenCalcResult(this, result)
    }

    companion object {
        val history: HistoryDataStore<FuroChanceShantenArgs> =
            HistoryDataStore("furoChanceShanten", typeOf<FuroChanceShantenArgs>())
    }
}

data class FuroChanceShantenCalcResult(
    val args: FuroChanceShantenArgs,
    val result: FuroChanceShantenResult
)
