package io.ssttkkl.mahjongutils.app.models.shanten

import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.serialization.Serializable
import mahjongutils.models.Tile
import mahjongutils.shanten.CommonShantenResult
import mahjongutils.shanten.regularShanten
import mahjongutils.shanten.shanten
import kotlin.reflect.typeOf

enum class ShantenMode {
    Union, Regular
}

@Serializable
data class ShantenArgs(
    val tiles: List<Tile>,
    val mode: ShantenMode
) {
    fun calc(): ShantenCalcResult {
        val result = when (mode) {
            ShantenMode.Union -> shanten(tiles)
            ShantenMode.Regular -> regularShanten(tiles)
        }
        return ShantenCalcResult(this, result)
    }

    companion object {
        private const val DATASTORE_FILENAME = "shanten.json"

        val history: HistoryDataStore<ShantenArgs> by lazy {
            HistoryDataStore(typeOf<ShantenArgs>()) { it / DATASTORE_FILENAME }
        }
    }
}

data class ShantenCalcResult(
    val args: ShantenArgs,
    val result: CommonShantenResult<*>
)