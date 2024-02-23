package io.ssttkkl.mahjongutils.app.models

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer

actual fun <T> createDataStore(
    identifer: String,
    groupIdenifier: String?,
    defaultValue: T,
    serializer: KSerializer<T>
): DataStore<T> = object : DataStore<T> {
    private val _data = MutableStateFlow(defaultValue)
    override val data: Flow<T>
        get() = _data

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        _data.update { transform(it) }
        return _data.value
    }
}
