package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.navigator.RouteInfo
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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