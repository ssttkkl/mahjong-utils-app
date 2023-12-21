package io.ssttkkl.mahjongutils.app.utils.os

import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual object FileUtils {
    @OptIn(ExperimentalForeignApi::class)
    actual val sandboxPath: Path by lazy {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        checkNotNull(documentDirectory?.path).toPath()
    }

    actual val sysFileSystem: FileSystem
        get() = FileSystem.SYSTEM
}