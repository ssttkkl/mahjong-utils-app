package io.ssttkkl.mahjongutils.app.models.base

import io.ssttkkl.mahjongutils.app.models.DataStore
import io.ssttkkl.mahjongutils.app.models.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KType

class HistoryDataStore<T>(
    val identifier: String,
    type: KType,
    private val maxItem: Int = DEFAULT_MAX_ITEM,
) {
    companion object {
        const val DEFAULT_MAX_ITEM = 100
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private val dataStore: DataStore<List<History<T>>> = run {
        val tSerializer = serializer(type)
        val historySerializer = serializer(History::class, listOf(tSerializer), false)
        val listSerializer = serializer(List::class, listOf(historySerializer), false)

        createDataStore(
            identifier,
            "history",
            emptyList(),
            listSerializer as KSerializer<List<History<T>>>
        )
    }

    suspend fun insert(args: T) {
        dataStore.updateData {
            if (it.firstOrNull()?.args == args) {
                // 要插入的和第一条记录一样，更新第一条记录的时间，其他不变
                listOf(History(args)) + it.drop(1)
            } else {
                // 插入最前面，并留下前100条
                (listOf(History(args)) + it).take(maxItem)
            }
        }
    }

    suspend fun clear() {
        dataStore.updateData { emptyList() }
    }

    val data: Flow<List<History<T>>>
        get() = dataStore.data
}
