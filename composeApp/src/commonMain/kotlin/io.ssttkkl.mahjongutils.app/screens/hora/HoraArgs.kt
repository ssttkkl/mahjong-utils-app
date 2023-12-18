package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import mahjongutils.hora.Hora
import mahjongutils.hora.hora
import mahjongutils.models.Furo
import mahjongutils.models.Tile
import mahjongutils.models.Wind
import mahjongutils.shanten.CommonShantenResult
import mahjongutils.shanten.regularShanten
import mahjongutils.shanten.shanten
import mahjongutils.yaku.Yaku

@Serializable
@Immutable
data class HoraArgs(
    val tiles: List<Tile>,
    val furo: List<Furo>,
    val agari: Tile,
    val tsumo: Boolean,
    val dora: Int,
    val selfWind: Wind?,
    val roundWind: Wind?,
    val extraYaku: Set<Yaku>
) {
    fun calc(): HoraCalcResult {
        val result = hora(tiles, furo, agari, tsumo, dora, selfWind, roundWind, extraYaku)
        return HoraCalcResult(this, result)
    }
}

data class HoraCalcResult(
    val args: HoraArgs,
    val result: Hora
)