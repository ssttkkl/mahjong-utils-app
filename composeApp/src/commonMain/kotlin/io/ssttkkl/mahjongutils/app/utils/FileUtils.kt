package io.ssttkkl.mahjongutils.app.utils

import okio.FileSystem
import okio.Path

expect object FileUtils {
    val sandboxPath: Path

    val sysFileSystem: FileSystem
}

val FileUtils.historyPath
    get() = sandboxPath / "history"
