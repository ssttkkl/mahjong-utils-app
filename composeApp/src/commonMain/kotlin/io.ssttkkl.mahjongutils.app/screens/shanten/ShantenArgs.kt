package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import mahjongutils.models.Tile
import mahjongutils.shanten.CommonShantenResult
import mahjongutils.shanten.regularShanten
import mahjongutils.shanten.shanten

enum class ShantenMode {
    Union, Regular
}

@Serializable
@Immutable
data class ShantenArgs(
    val tiles: List<Tile>,
    val mode: ShantenMode
) {
    fun calc(): CommonShantenResult<*> {
        return when (mode) {
            ShantenMode.Union -> shanten(tiles)
            ShantenMode.Regular -> regularShanten(tiles)
        }
    }
}