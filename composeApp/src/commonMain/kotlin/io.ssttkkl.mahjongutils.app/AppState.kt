package io.ssttkkl.mahjongutils.app

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator

data class AppState(
    val windowSizeClass: WindowSizeClass,
    val mainPaneNavigator: Navigator,
    val subPaneNavigator: Navigator?
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppState(mainPaneNavigator: Navigator, subPaneNavigator: Navigator?): AppState {
    val windowSizeClass = calculateWindowSizeClass()

    return remember(windowSizeClass, mainPaneNavigator, subPaneNavigator) {
        AppState(windowSizeClass, mainPaneNavigator, subPaneNavigator)
    }
}

val LocalAppState = compositionLocalOf<AppState> {
    error("No LocalAppState provided!")
}
