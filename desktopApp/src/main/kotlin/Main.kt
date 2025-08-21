import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.sentry.SendCachedEnvelopeFireAndForgetIntegration
import io.sentry.SendFireAndForgetEnvelopeSender
import io.sentry.Sentry
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.base.utils.FileUtils
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalMainWindowState
import io.ssttkkl.mahjongutils.app.getAppTypography
import io.ssttkkl.mahjongutils.app.base.utils.SentryConfig
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import mahjongutils.composeapp.generated.resources.icon_app
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


fun init(args: Array<String>) {
    val logger = LoggerFactory.getLogger("init")
    logger.info("App start")

    if (!args.contains("--disableSentry")) {
        logger.info("Sentry: enabled")
        initSentry()
        logger.info("Sentry init success")
    } else {
        logger.info("Sentry: disabled")
    }

    logger.info("UserDataDir: ${FileUtils.sandboxPath}")
}

private fun initSentry() {
    Sentry.init { options ->
        options.dsn = SentryConfig.dsn
        options.release = SentryConfig.release

        options.tags["os"] = System.getProperty("os.name") + " " + System.getProperty("os.version")
        options.tags["arch"] = System.getProperty("os.arch")

        options.isEnableUserInteractionTracing = true
        options.isEnableUserInteractionBreadcrumbs = true

        options.cacheDirPath = (FileUtils.sandboxPath / "sentryCache").toFile().absolutePath
        options.addIntegration(
            SendCachedEnvelopeFireAndForgetIntegration(
                SendFireAndForgetEnvelopeSender { options.cacheDirPath })
        )
    }
}

fun main(args: Array<String>) = application {
    init(args)

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