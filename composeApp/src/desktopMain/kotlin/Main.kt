import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.MR

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = StringDesc.Resource(MR.strings.app_name).localized()
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}