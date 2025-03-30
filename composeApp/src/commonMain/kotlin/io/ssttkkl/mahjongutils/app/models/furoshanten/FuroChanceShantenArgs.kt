package io.ssttkkl.mahjongutils.app.models.furoshanten

import androidx.compose.runtime.Immutable
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ssttkkl.mahjongutils.app.base.utils.logger
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.serialization.Serializable
import mahjongutils.models.Tile
import mahjongutils.shanten.FuroChanceShantenResult
import mahjongutils.shanten.furoChanceShanten
import kotlin.reflect.typeOf

@Serializable
@Immutable
data class FuroChanceShantenArgs(
    val tiles: List<Tile>,
    val chanceTile: Tile,
    val allowChi: Boolean = true
) {
    fun calc(): FuroChanceShantenCalcResult {
        logger.info("furoChanceShanten calc args: ${this}")
        val result = furoChanceShanten(tiles, chanceTile, allowChi)
        logger.info("furoChanceShanten calc result: ${result}")
        return FuroChanceShantenCalcResult(this, result)
    }

    companion object {
        private val logger = KotlinLogging.logger(FuroChanceShantenArgs::class)
        val history: HistoryDataStore<FuroChanceShantenArgs> =
            HistoryDataStore("furoChanceShanten", typeOf<FuroChanceShantenArgs>())
    }
}

@Immutable
data class FuroChanceShantenCalcResult(
    val args: FuroChanceShantenArgs,
    val result: FuroChanceShantenResult
)
