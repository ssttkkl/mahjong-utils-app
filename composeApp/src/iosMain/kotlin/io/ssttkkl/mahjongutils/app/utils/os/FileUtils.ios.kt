package io.ssttkkl.mahjongutils.app.utils.os

import okio.FileSystem
import okio.Path

actual object FileUtils {
    actual val sandboxPath: Path
        get() = TODO("Not yet implemented")
    actual val sysFileSystem: FileSystem
        get() = FileSystem.SYSTEM
}