package io.ssttkkl.mahjongutils.app.models.hora

import androidx.compose.runtime.Immutable
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import mahjongutils.hora.Hora
import mahjongutils.hora.hora
import mahjongutils.models.Furo
import mahjongutils.models.Tile
import mahjongutils.models.Wind
import mahjongutils.yaku.Yaku
import kotlin.reflect.typeOf

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

    companion object {
        private const val DATASTORE_FILENAME = "hora.json"

        val history: HistoryDataStore<HoraArgs> by lazy {
            HistoryDataStore(typeOf<HoraArgs>()) { it / DATASTORE_FILENAME }
        }
    }
}

data class HoraCalcResult(
    val args: HoraArgs,
    val result: Hora
)