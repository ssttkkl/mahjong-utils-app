package io.ssttkkl.mahjongutils.app.base.utils

import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set

actual fun <T> createDataStore(
    identifer: String,
    groupIdenifier: String?,
    defaultValue: T,
    serializer: KSerializer<T>
): DataStore<T> = object : DataStore<T> {
    private val localStorageKey = "${groupIdenifier}/${identifer}"

    private val _data: MutableStateFlow<T>

    init {
        val initialValue = localStorage[localStorageKey]?.let {
            Json.decodeFromString(serializer, it)
        } ?: defaultValue

        _data = MutableStateFlow(initialValue)
    }
    override val data: Flow<T>
        get() = _data

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        val updatedValue = transform(_data.value)
        localStorage[localStorageKey] = Json.encodeToString(serializer, updatedValue)
        _data.value = updatedValue
        return updatedValue
    }
}