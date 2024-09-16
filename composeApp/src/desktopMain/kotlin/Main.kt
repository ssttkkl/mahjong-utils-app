import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppNavigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppNavigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.Url
import io.ssttkkl.mahjongutils.app.getAppTypography
import io.ssttkkl.mahjongutils.app.init.AppInit
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import io.ssttkkl.mahjongutils.app.utils.url.URLDecoder
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import mahjongutils.composeapp.generated.resources.icon_app
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.net.URI


private fun parseQuery(query: String): Map<String, String> {
    val queryPairs: MutableMap<String, String> = HashMap()
    val pairs = query.split("&").filter { it.isNotEmpty() }
    for (pair in pairs) {
        val idx = pair.indexOf("=")
        queryPairs[URLDecoder.decode(pair.substring(0, idx))] =
            URLDecoder.decode(pair.substring(idx + 1))
    }
    return queryPairs
}

@Composable
private fun UrlHandler(appNavigator: AppNavigator, uri: URI? = null) {
    val logger = remember { LoggerFactory.getLogger("UrlHandler") }

    LaunchedEffect(uri) {
        if (uri != null && uri.scheme == "mahjongutils") {
            appNavigator.url = Url(uri.path, uri.query?.let { parseQuery(it) } ?: emptyMap())
            logger.info("apply url: ${appNavigator.url}")
        }
    }
}

fun main(args: Array<String>) = application {
    println("argc: ${args.size}")
    println("argv: ${args.joinToString(" ")}")
    AppInit.doInit()
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.icon_app)
    ) {
        App(typography = getAppTypography()) {
            val appNavigator = LocalAppNavigator.currentOrThrow

            UrlHandler(appNavigator, args.getOrNull(0)?.let { URI(it) })
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App(typography = getAppTypography())
}