@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ssttkkl.mahjongutils.app.App
import mahjongutils.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.icon_app)
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}