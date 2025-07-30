package mahjongutils.app.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.rememberWindowState
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalMainWindowState
import io.ssttkkl.mahjongutils.app.getAppTypography

@Composable
actual fun TestApp() {
    val windowState = rememberWindowState()
    CompositionLocalProvider(LocalMainWindowState provides windowState) {
        App(typography = getAppTypography())
    }
}