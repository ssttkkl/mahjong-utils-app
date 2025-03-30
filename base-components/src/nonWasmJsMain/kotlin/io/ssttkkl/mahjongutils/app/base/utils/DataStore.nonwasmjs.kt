package io.ssttkkl.mahjongutils.app.base.utils

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
private class JsonOkioSerializer<T>(
    override val defaultValue: T,
    val serializer: KSerializer<T>,
    val json: Json = Json,
) : OkioSerializer<T> {
    override suspend fun readFrom(source: BufferedSource): T {
        return json.decodeFromBufferedSource(serializer, source)
    }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        json.encodeToBufferedSink(serializer, t, sink)
    }
}

actual fun <T> createDataStore(
    identifer: String,
    groupIdenifier: String?,
    defaultValue: T,
    serializer: KSerializer<T>
): DataStore<T> {
    val androidxDatastore = DataStoreFactory.create(
        OkioStorage(
            FileUtils.sysFileSystem,
            JsonOkioSerializer(defaultValue, serializer),
            producePath = {
                if (groupIdenifier == null)
                    FileUtils.sandboxPath / "$identifer.json"
                else
                    FileUtils.sandboxPath / groupIdenifier / "$identifer.json"
            }
        )
    )

    return object : DataStore<T> {
        override val data: Flow<T>
            get() = androidxDatastore.data

        override suspend fun updateData(transform: suspend (t: T) -> T): T =
            androidxDatastore.updateData(transform)
    }
}
