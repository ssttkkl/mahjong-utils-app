package io.ssttkkl.mahjongutils.app.init

import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import io.ssttkkl.mahjongutils.app.utils.log.WasmJsLoggerFactory

internal actual val AppInit.platformModules: List<InitModule>
    get() = listOf()

internal actual val LogInit = InitModule(1) {
    LoggerFactory.impl = WasmJsLoggerFactory
}