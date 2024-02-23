import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.MR

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(MR.strings.app_name),
        icon = painterResource(MR.images.icon_app)
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}