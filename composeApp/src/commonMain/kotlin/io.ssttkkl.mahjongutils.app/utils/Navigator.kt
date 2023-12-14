package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.query
import kotlin.reflect.KType
import kotlin.reflect.typeOf

suspend fun Navigator.navigate(
    route: String,
    params: Map<String, TypedValue>,
    options: NavOptions? = null
) {
    val navigateRoute = withContext(Dispatchers.Default) {
        if (params.isNotEmpty()) {
            "${route}?" + params.entries.joinToString("&") { (key, typed) ->
                buildString {
                    append(key)
                    append("=")
                    if (typed.type != typeOf<String>()) {
                        append(
                            Json.encodeToString(
                                serializer(typed.type),
                                typed.value
                            )
                        )
                    } else {
                        append(typed.unwrap<String>())
                    }
                }
            }
        } else {
            route
        }
    }

    navigate(navigateRoute, options)
}


@Composable
fun BackStackEntry.useParams(
    paramsType: Map<String, KType>,
    withParams: @Composable (Map<String, Any?>?) -> Unit,
) {
    var params = remember { mutableStateMapOf<String, Any?>() }
    var ok by remember { mutableStateOf(false) }

    // 异步反序列化
    val rawQuery = paramsType.keys.associateWith { query<String>(it) }
        .filterValues { it != null }
        .mapValues { (_, it) -> it!! }

    LaunchedEffect(rawQuery) {
        withContext(Dispatchers.Default) {
            rawQuery.forEach { (key, rawParam) ->
                val type = paramsType[key]!!
                params[key] = if (type == typeOf<String>()) {
                    rawParam
                } else {
                    Json.decodeFromString(serializer(type), rawParam)
                }
            }
            ok = true
        }
    }

    if (ok) {
        withParams(params)
    } else {
        withParams(null)
    }
}