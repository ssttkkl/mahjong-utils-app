package io.ssttkkl.mahjongutils.app.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer
import okio.Path

actual interface DataStore<T> {
    actual val data: Flow<T>
    actual suspend fun updateData(transform: suspend (t: T) -> T): T
}

actual fun <T> createDatastore(
    defaultValue: T,
    serializer: KSerializer<T>,
    producePath: () -> Path
): DataStore<T> = object : DataStore<T> {
    private val _data = MutableStateFlow(defaultValue)
    override val data: Flow<T>
        get() = _data

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        _data.update { transform(it) }
        return _data.value
    }
}
