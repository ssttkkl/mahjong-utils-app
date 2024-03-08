@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.getAppTypography
import io.ssttkkl.mahjongutils.app.init.AppInit
import kotlinx.browser.document
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.w3c.dom.asList


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    AppInit.doInit()
    CanvasBasedWindow(
        canvasElementId = "ComposeTarget"
    ) {
        val title = stringResource(Res.string.app_name)
        LaunchedEffect(title) {
            document.title = title
        }

        App(typography = getAppTypography())

        LaunchedEffect(Unit) {
            document.getElementById("loading-hint")?.remove()
        }
    }
}