package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.ssttkkl.mahjongutils.app.LocalAppState
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.navigator.RouteInfo
import io.ssttkkl.mahjongutils.app.utils.TypedValue
import io.ssttkkl.mahjongutils.app.utils.navigate
import kotlinx.coroutines.launch
import kotlin.reflect.KType


object RouteFuroShanten : RouteInfo {
    override val title: String
        get() = Res.string.title_furo_shanten

    override val route: String
        get() = "/furo_shanten"

    override val paramsType: Map<String, KType> = emptyMap()

    @Composable
    override fun content(params: Map<String, Any?>) {
        val appState = LocalAppState.current
        val navigator = appState.subPaneNavigator ?: appState.mainPaneNavigator
        val scope = rememberCoroutineScope()
        FuroShantenScreen { args ->
            scope.launch {
                navigator.navigate(
                    RouteFuroShantenResult.route,
                    mapOf("args" to TypedValue.of(args))
                )
            }
        }
    }
}

