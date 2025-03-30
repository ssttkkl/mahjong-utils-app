package io.ssttkkl.mahjongutils.app.base.utils

import net.harawata.appdirs.AppDirsFactory
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

actual object FileUtils {
    actual val sandboxPath: Path by lazy {
        AppDirsFactory.getInstance().getUserDataDir("mahjongutils", null, "ssttkkl").toPath()
    }

    actual val sysFileSystem: FileSystem
        get() = FileSystem.SYSTEM
}