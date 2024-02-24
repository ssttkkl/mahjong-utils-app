package io.ssttkkl.mahjongutils.app.models.hora

import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.serialization.Serializable
import mahjongutils.hora.Hora
import mahjongutils.hora.HoraOptions
import mahjongutils.hora.hora
import mahjongutils.models.Furo
import mahjongutils.models.Tile
import mahjongutils.models.Wind
import mahjongutils.yaku.DefaultYakuSerializer
import mahjongutils.yaku.Yaku
import kotlin.reflect.typeOf

@Serializable
data class HoraArgs(
    val tiles: List<Tile>,
    val furo: List<Furo> = emptyList(),
    val agari: Tile,
    val tsumo: Boolean,
    val dora: Int = 0,
    val selfWind: Wind? = null,
    val roundWind: Wind? = null,
    val extraYaku: Set<@Serializable(DefaultYakuSerializer::class) Yaku> = emptySet(),
    val options: HoraOptions = HoraOptions.Default
) {
    fun calc(): HoraCalcResult {
        val result = hora(tiles, furo, agari, tsumo, dora, selfWind, roundWind, extraYaku, options)
        return HoraCalcResult(this, result)
    }

    companion object {
        val history: HistoryDataStore<HoraArgs> =
            HistoryDataStore("hora", typeOf<HoraArgs>())
    }
}

data class HoraCalcResult(
    val args: HoraArgs,
    val result: Hora
)