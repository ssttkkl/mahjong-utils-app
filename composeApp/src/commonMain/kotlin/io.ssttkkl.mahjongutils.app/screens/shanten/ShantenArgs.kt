package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import mahjongutils.models.Furo
import mahjongutils.models.Tile
import mahjongutils.shanten.UnionShantenResult
import mahjongutils.shanten.shanten

@Serializable
@Immutable
data class ShantenArgs(
    val tiles: List<Tile>,
    val furo: List<Furo> = emptyList(),
    val bestShantenOnly: Boolean = false
) {
    fun calc(): UnionShantenResult {
        return shanten(tiles, furo, bestShantenOnly)
    }
}