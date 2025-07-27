import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.base.utils.FileUtils
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.getAppTypography
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import mahjongutils.composeapp.generated.resources.icon_app
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

val LocalMainWindowState = compositionLocalOf<WindowState> {
    error("CompositionLocal LocalMainWindowState not present")
}

fun init() {
    val logger = LoggerFactory.getLogger("init")
    logger.info("App start")
    logger.info("UserDataDir: ${FileUtils.sandboxPath}")
}

fun main() = application {
    init()

    val windowState = rememberWindowState()
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.icon_app)
    ) {
        CompositionLocalProvider(LocalMainWindowState provides windowState) {
            App(typography = getAppTypography())
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App(typography = getAppTypography())
}