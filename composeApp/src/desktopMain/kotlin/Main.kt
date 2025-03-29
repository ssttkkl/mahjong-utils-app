import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.getAppTypography
import io.ssttkkl.mahjongutils.app.init.AppInit
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.stringResource
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.inputStream

fun main() = application {
    application {
        // app.dir is set when packaged to point at our collected inputs.
        val appIcon = remember {
            System.getProperty("app.dir")
                ?.let { Paths.get(it, "icon-512.png") }
                ?.takeIf { it.exists() }
                ?.inputStream()
                ?.buffered()
                ?.use { BitmapPainter(it.readAllBytes().decodeToImageBitmap()) }
        }

        AppInit.doInit()
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            icon = appIcon
        ) {
            App(typography = getAppTypography())
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App(typography = getAppTypography())
}