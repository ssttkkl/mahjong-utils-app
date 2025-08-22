package io.ssttkkl.mahjongutils.app.kuikly.components

import androidx.compose.runtime.mutableStateListOf
import io.ssttkkl.mahjongutils.app.kuikly.screens.about.AboutScreen

object ScreenRegistry {
    val navigationScreens = mutableStateListOf<Screen>()

    init {
        navigationScreens.add(AboutScreen)
    }
}