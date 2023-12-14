package io.ssttkkl.mahjongutils.app.screens.furoshanten

import moe.tlaster.precompose.navigation.RouteBuilder

const val ROUTE_FURO_SHANTEN = "/furoShanten"
const val ROUTE_FURO_SHANTEN_RESULT = "/furoShanten/result"

fun RouteBuilder.furoShantenScene() {
    scene(ROUTE_FURO_SHANTEN) {
        FuroShantenScreen()
    }
    scene(ROUTE_FURO_SHANTEN_RESULT) {
        FuroShantenResultScreen()
    }
}