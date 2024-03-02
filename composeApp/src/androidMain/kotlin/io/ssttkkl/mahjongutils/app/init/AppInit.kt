package io.ssttkkl.mahjongutils.app.init

import io.ssttkkl.mahjongutils.app.MyApp
import io.ssttkkl.mahjongutils.app.utils.FileUtils
import okio.Path.Companion.toOkioPath

internal actual val AppInit.platformModules: List<InitModule>
    get() = listOf(FileUtilsInit)


private val FileUtilsInit = InitModule(1) {
    FileUtils.setSandboxPath(MyApp.current.filesDir!!.toOkioPath())
}