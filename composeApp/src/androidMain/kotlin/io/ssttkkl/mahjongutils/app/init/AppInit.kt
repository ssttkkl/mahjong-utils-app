package io.ssttkkl.mahjongutils.app.init

import io.ssttkkl.mahjongutils.app.MyApp
import io.ssttkkl.mahjongutils.app.utils.FileUtils
import io.ssttkkl.mahjongutils.app.utils.log.AndroidLoggerFactory
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import okio.Path.Companion.toOkioPath

internal actual val AppInit.platformModules: List<InitModule>
    get() = listOf(FileUtilsInit)

internal actual val LogInit = InitModule(1) {
    LoggerFactory.impl = AndroidLoggerFactory
}

private val FileUtilsInit = InitModule(1) {
    FileUtils.setSandboxPath(MyApp.current.filesDir!!.toOkioPath())
}