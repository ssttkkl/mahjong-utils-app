package io.ssttkkl.mahjongutils.app.models.shanten

import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
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
    val mode: ShantenMode = ShantenMode.Union
) {
    fun calc(): ShantenCalcResult {
        logger.info("shanten calc args: ${this}")
        val result = when (mode) {
            ShantenMode.Union -> shanten(tiles)
            ShantenMode.Regular -> regularShanten(tiles)
        }
        logger.info("shanten calc result: ${result}")
        return ShantenCalcResult(this, result)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ShantenArgs::class)
        val history: HistoryDataStore<ShantenArgs> =
            HistoryDataStore("shanten", typeOf<ShantenArgs>())
    }
}

data class ShantenCalcResult(
    val args: ShantenArgs,
    val result: CommonShantenResult<*>
)