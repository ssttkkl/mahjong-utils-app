package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowState

val LocalMainWindowState = compositionLocalOf<WindowState> {
    error("CompositionLocal LocalMainWindowState not present")
}

@Composable
actual fun rememberAppStateExtra(): Map<String, Any?> {
    val mainWindowState = LocalMainWindowState.current
    return remember(mainWindowState) {
        mapOf(
            "MainWindowState" to mainWindowState
        )
    }
}

val AppState.mainWindowState: WindowState
    get() = extra["MainWindowState"] as WindowState
