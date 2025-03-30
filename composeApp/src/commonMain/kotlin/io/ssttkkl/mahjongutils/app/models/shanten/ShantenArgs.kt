package io.ssttkkl.mahjongutils.app.models.shanten

import androidx.compose.runtime.Immutable
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ssttkkl.mahjongutils.app.base.utils.logger
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.serialization.Serializable
import mahjongutils.models.Tile
import mahjongutils.shanten.CommonShantenResult
import mahjongutils.shanten.regularShanten
import mahjongutils.shanten.shanten
import kotlin.reflect.typeOf

@Immutable
enum class ShantenMode {
    Union, Regular
}

@Serializable
@Immutable
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
        private val logger = KotlinLogging.logger(ShantenArgs::class)
        val history: HistoryDataStore<ShantenArgs> =
            HistoryDataStore("shanten", typeOf<ShantenArgs>())
    }
}

@Immutable
data class ShantenCalcResult(
    val args: ShantenArgs,
    val result: CommonShantenResult<*>
)