package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import mahjongutils.models.Tile
import mahjongutils.shanten.FuroChanceShantenResult
import mahjongutils.shanten.furoChanceShanten

@Serializable
@Immutable
data class FuroChanceShantenArgs(
    val tiles: List<Tile>,
    val chanceTile: Tile,
    val allowChi: Boolean = true
) {
    fun calc(): FuroChanceShantenResult {
        return furoChanceShanten(tiles, chanceTile, allowChi)
    }
}