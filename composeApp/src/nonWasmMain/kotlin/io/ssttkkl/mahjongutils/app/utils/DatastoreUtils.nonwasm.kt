package io.ssttkkl.mahjongutils.app.utils

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.BufferedSink
import okio.BufferedSource
import okio.Path

actual typealias DataStore<T> = androidx.datastore.core.DataStore<T>

@OptIn(ExperimentalSerializationApi::class)
class JsonOkioSerializer<T>(
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

fun <T> KSerializer<T>.toOkioSerializer(
    defaultValue: T,
    json: Json = Json
): OkioSerializer<T> =
    JsonOkioSerializer(defaultValue, this, json)

actual fun <T> createDatastore(
    defaultValue: T,
    serializer: KSerializer<T>,
    producePath: () -> Path
): DataStore<T> =
    DataStoreFactory.create(
        OkioStorage(
            FileUtils.sysFileSystem,
            serializer.toOkioSerializer(defaultValue),
            producePath = producePath
        )
    )
