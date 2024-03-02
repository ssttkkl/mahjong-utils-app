package io.ssttkkl.mahjongutils.app.models.hanhu

import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import mahjongutils.hanhu.HanHuOptions
import mahjongutils.hanhu.getChildPointByHanHu
import mahjongutils.hanhu.getParentPointByHanHu

data class HanHuArgs(val han: Int, val hu: Int, val options: HanHuOptions) {
    fun calc(): HanHuResult {
        logger.info("hanhu calc args: ${this}")
        val res = HanHuResult(
            getParentPointByHanHu(han, hu, options),
            getChildPointByHanHu(han, hu, options),
        )
        logger.info("hanhu calc result: ${res}")
        return res
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HanHuArgs::class)
    }
}