package io.ssttkkl.mahjongutils.app.base.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

interface DataStore<T> {
    val data: Flow<T>
    suspend fun updateData(transform: suspend (t: T) -> T): T
}

expect fun <T> createDataStore(
    identifer: String,
    groupIdenifier: String?,
    defaultValue: T,
    serializer: KSerializer<T>
): DataStore<T>

inline fun <reified T> createDataStore(
    identifer: String,
    groupIdenifier: String?,
    defaultValue: T
): DataStore<T> =
    createDataStore(identifer, groupIdenifier, defaultValue, serializer<T>())