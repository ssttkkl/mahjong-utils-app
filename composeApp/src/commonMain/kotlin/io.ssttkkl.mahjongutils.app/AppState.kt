package io.ssttkkl.mahjongutils.app

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator

data class AppState(
    val windowSizeClass: WindowSizeClass,
    val mainPaneNavigator: Navigator,
    val subPaneNavigator: Navigator?
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppState(): AppState {
    val windowSizeClass = calculateWindowSizeClass()
    val mainPaneNavigator = rememberNavigator()
    val subPaneNavigator = if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
        rememberNavigator()
    } else {
        null
    }

    return remember(windowSizeClass, mainPaneNavigator, subPaneNavigator) {
        AppState(windowSizeClass, mainPaneNavigator, subPaneNavigator)
    }
}

val LocalAppState = compositionLocalOf<AppState> {
    error("No LocalAppState provided!")
}
