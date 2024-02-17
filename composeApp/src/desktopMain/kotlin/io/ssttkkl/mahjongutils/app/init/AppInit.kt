package io.ssttkkl.mahjongutils.app.init

import io.ssttkkl.mahjongutils.app.utils.log.JvmLoggerFactory
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory

internal actual val AppInit.platformModules: List<InitModule>
    get() = listOf()

internal actual val LogInit = InitModule(1) {
    LoggerFactory.impl = JvmLoggerFactory
}