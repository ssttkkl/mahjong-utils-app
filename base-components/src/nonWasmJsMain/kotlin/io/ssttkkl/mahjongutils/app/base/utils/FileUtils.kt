package io.ssttkkl.mahjongutils.app.base.utils

import okio.FileSystem
import okio.Path

expect object FileUtils {
    val sandboxPath: Path

    val sysFileSystem: FileSystem
}

