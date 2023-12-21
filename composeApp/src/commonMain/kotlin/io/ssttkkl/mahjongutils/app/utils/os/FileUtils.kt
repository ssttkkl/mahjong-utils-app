package io.ssttkkl.mahjongutils.app.utils.os

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path

expect object FileUtils {
    val sandboxPath: Path

    val sysFileSystem: FileSystem
}

val FileUtils.historyPath
    get() = sandboxPath / "history"
