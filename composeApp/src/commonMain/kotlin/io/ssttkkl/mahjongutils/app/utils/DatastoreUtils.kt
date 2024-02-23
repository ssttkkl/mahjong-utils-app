package io.ssttkkl.mahjongutils.app.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import okio.Path

expect interface DataStore<T> {
    /**
     * Provides efficient, cached (when possible) access to the latest durably persisted state.
     * The flow will always either emit a value or throw an exception encountered when attempting
     * to read from disk. If an exception is encountered, collecting again will attempt to read the
     * data again.
     *
     * Do not layer a cache on top of this API: it will be be impossible to guarantee consistency.
     * Instead, use data.first() to access a single snapshot.
     *
     * @return a flow representing the current state of the data
     * @throws IOException when an exception is encountered when reading data
     */
    public val data: Flow<T>

    /**
     * Updates the data transactionally in an atomic read-modify-write operation. All operations
     * are serialized, and the transform itself is a coroutine so it can perform heavy work
     * such as RPCs.
     *
     * The coroutine completes when the data has been persisted durably to disk (after which
     * [data] will reflect the update). If the transform or write to disk fails, the
     * transaction is aborted and an exception is thrown.
     *
     * @return the snapshot returned by the transform
     * @throws IOException when an exception is encountered when writing data to disk
     * @throws Exception when thrown by the transform function
     */
    public suspend fun updateData(transform: suspend (t: T) -> T): T
}

expect fun <T> createDatastore(
    defaultValue: T,
    serializer: KSerializer<T>,
    producePath: () -> Path
): DataStore<T>

inline fun <reified T> createDatastore(
    defaultValue: T,
    noinline producePath: () -> Path
): DataStore<T> =
    createDatastore(defaultValue, serializer<T>(), producePath)