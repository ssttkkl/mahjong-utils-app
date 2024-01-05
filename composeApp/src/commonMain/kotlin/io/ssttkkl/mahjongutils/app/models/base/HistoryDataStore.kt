package io.ssttkkl.mahjongutils.app.models.base

import androidx.datastore.core.DataStore
import io.ssttkkl.mahjongutils.app.utils.FileUtils
import io.ssttkkl.mahjongutils.app.utils.createDatastore
import io.ssttkkl.mahjongutils.app.utils.historyPath
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import okio.Path
import kotlin.reflect.KType

class HistoryDataStore<T>(
    type: KType,
    private val maxItem: Int = DEFAULT_MAX_ITEM,
    producePath: (Path) -> Path
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

        createDatastore(
            emptyList(),
            listSerializer as KSerializer<List<History<T>>>
        ) {
            producePath(FileUtils.historyPath)
        }
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
