package io.ssttkkl.mahjongutils.app.utils.os

import okio.FileSystem
import okio.Path

actual object FileUtils {
    private var _sandboxPath: Path? = null
    actual val sandboxPath: Path
        get() = checkNotNull(_sandboxPath) {
            "sandboxPath is not present"
        }

    fun setSandboxPath(path: Path) {
        _sandboxPath = path
    }

    actual val sysFileSystem: FileSystem
        get() = FileSystem.SYSTEM
}