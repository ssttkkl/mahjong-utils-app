import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.sentry.SendCachedEnvelopeFireAndForgetIntegration
import io.sentry.SendFireAndForgetEnvelopeSender
import io.sentry.Sentry
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.BuildKonfig
import io.ssttkkl.mahjongutils.app.base.utils.FileUtils
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.getAppTypography
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import mahjongutils.composeapp.generated.resources.icon_app
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun init(args: Array<String>) {
    val logger = LoggerFactory.getLogger("init")
    logger.info("App start")

    if (!args.contains("--disableSentry")) {
        logger.info("sentry is enabled")
        initSentry()
        logger.info("sentry init success")
    } else {
        logger.info("sentry is disabled")
    }

    logger.info("UserDataDir: ${FileUtils.sandboxPath}")
}

private fun initSentry() {
    Sentry.init { options ->
        options.dsn = BuildKonfig.SENTRY_DSN

        options.release =
            "${BuildKonfig.APPLICATION_ID}@${BuildKonfig.VERSION_NAME}+${BuildKonfig.GIT_COMMIT_HASH}"

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
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.icon_app)
    ) {
        App(typography = getAppTypography())
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App(typography = getAppTypography())
}