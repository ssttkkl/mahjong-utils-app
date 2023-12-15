package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.navigator.RouteInfo
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object RouteFuroShantenResult : RouteInfo {
    override val title: String
        get() = Res.string.title_furo_shanten_result

    override val route: String
        get() = "/furo_shanten/result"

    override val paramsType: Map<String, KType> = mapOf(
        "args" to typeOf<FuroChanceShantenArgs>()
    )

    @Composable
    override fun content(params: Map<String, Any?>) {
        FuroShantenResultScreen(params["args"] as FuroChanceShantenArgs)
    }
}