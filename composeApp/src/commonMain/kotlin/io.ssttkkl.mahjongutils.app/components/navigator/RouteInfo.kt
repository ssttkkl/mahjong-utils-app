package io.ssttkkl.mahjongutils.app.components.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.query
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface RouteInfo {
    val route: String

    val title: String

    val icon: ImageVector?
        get() = null

    val paramsType: Map<String, KType>

    @Composable
    fun content(params: Map<String, Any?>)
}

fun RouteBuilder.scene(routeInfo: RouteInfo) {
    scene(route = routeInfo.route) { entry ->
        var params: Map<String, Any?>? by remember { mutableStateOf(null) }

        // 异步反序列化
        val rawQuery = routeInfo.paramsType.keys.associateWith { entry.query<String>(it) }
            .filterValues { it != null }
            .mapValues { (_, it) -> it!! }

        LaunchedEffect(rawQuery) {
            withContext(Dispatchers.Default) {
                params = buildMap {
                    rawQuery.forEach { (key, rawParam) ->
                        val type = routeInfo.paramsType[key]!!
                        this[key] = if (type == typeOf<String>()) {
                            rawParam
                        } else {
                            Json.decodeFromString(serializer(type), rawParam)
                        }
                    }
                }
            }
        }

        params?.let {
            routeInfo.content(it)
        }
    }
}
