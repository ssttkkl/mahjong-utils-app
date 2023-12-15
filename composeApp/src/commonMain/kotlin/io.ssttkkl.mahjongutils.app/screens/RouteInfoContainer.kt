package io.ssttkkl.mahjongutils.app.screens

import io.ssttkkl.mahjongutils.app.screens.furoshanten.RouteFuroShanten
import io.ssttkkl.mahjongutils.app.screens.furoshanten.RouteFuroShantenResult
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShantenResult

val RouteInfoContainer = mapOf(
    RouteShanten.route to RouteShanten,
    RouteShantenResult.route to RouteShantenResult,
    RouteFuroShanten.route to RouteFuroShanten,
    RouteFuroShantenResult.route to RouteFuroShantenResult
)