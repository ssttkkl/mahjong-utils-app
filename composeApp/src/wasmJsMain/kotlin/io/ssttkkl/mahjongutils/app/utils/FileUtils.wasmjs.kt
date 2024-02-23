package io.ssttkkl.mahjongutils.app.utils

import okio.FileSystem
import okio.Path

actual object FileUtils {
    actual val sandboxPath: Path
        get() {
            error("not supported")
        }

    actual val sysFileSystem: FileSystem
        get() = error("not supported")
}