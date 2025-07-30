package mahjongutils.app.test

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.getAppTypography

@Composable
actual fun TestApp() {
    App(typography = getAppTypography())
}