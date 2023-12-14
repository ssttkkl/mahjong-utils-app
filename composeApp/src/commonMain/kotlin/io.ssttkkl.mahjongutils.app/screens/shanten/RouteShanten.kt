package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.ssttkkl.mahjongutils.app.LocalAppState
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.navigator.RouteInfo
import io.ssttkkl.mahjongutils.app.components.navigator.scene
import io.ssttkkl.mahjongutils.app.utils.TypedValue
import io.ssttkkl.mahjongutils.app.utils.navigate
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.RouteBuilder
import kotlin.reflect.KType
import kotlin.reflect.typeOf


object RouteShanten : RouteInfo {
    override val title: String
        get() = Res.string.title_shanten

    override val route: String
        get() = "/shanten"

    override val paramsType: Map<String, KType> = emptyMap()

    @Composable
    override fun content(params: Map<String, Any?>) {
        val navigator = LocalAppState.current.navigator
        val scope = rememberCoroutineScope()
        ShantenScreen { args ->
            scope.launch {
                navigator.navigate(RouteShantenResult.route, mapOf("args" to TypedValue.of(args)))
            }
        }
    }
}

object RouteShantenResult : RouteInfo {
    override val title: String
        get() = Res.string.title_shanten_result

    override val route: String
        get() = "/shanten/result"

    override val paramsType: Map<String, KType> = mapOf(
        "args" to typeOf<ShantenArgs>()
    )

    @Composable
    override fun content(params: Map<String, Any?>) {
        ShantenResultScreen(params["args"] as ShantenArgs)
    }
}

fun RouteBuilder.shantenScene() {
    scene(RouteShanten)
    scene(RouteShantenResult)
}