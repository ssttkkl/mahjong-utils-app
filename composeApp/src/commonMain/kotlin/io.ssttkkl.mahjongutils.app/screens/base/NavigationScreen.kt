package io.ssttkkl.mahjongutils.app.screens.base

import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource

interface NavigationScreen : Screen {
    val title: StringResource
}

