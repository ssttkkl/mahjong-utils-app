package io.ssttkkl.mahjongutils.app.screens

import io.ssttkkl.mahjongutils.app.screens.furoshanten.RouteFuroShanten
import io.ssttkkl.mahjongutils.app.screens.furoshanten.RouteFuroShantenResult
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShantenResult

private val CalcFormRouteInfo = mapOf(
    RouteShanten.route to RouteShanten,
    RouteFuroShanten.route to RouteFuroShanten,
)

private val CalcResultRouteInfo = mapOf(
    RouteShantenResult.route to RouteShantenResult,
    RouteFuroShantenResult.route to RouteFuroShantenResult
)

val NavigationRouteInfo = CalcFormRouteInfo

val AllRouteInfo = CalcFormRouteInfo + CalcResultRouteInfo

val MainPaneRouteInfo = CalcFormRouteInfo

val SubPaneRouteInfo = CalcResultRouteInfo