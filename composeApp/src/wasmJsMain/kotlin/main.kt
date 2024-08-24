@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppNavigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppNavigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.Url
import io.ssttkkl.mahjongutils.app.getAppTypography
import io.ssttkkl.mahjongutils.app.init.AppInit
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import kotlinx.browser.document
import kotlinx.browser.window
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

private fun extractUrl(fullUrl: String): Url? {
    val pathStartIdx = fullUrl.indexOf("#/") + 2
    if (pathStartIdx > 1) {
        return Url.parse(fullUrl.substring(pathStartIdx))
    } else {
        return null
    }
}

@Composable
private fun UrlHandler(appNavigator: AppNavigator) {
    val logger = remember { LoggerFactory.getLogger("UrlHandler") }

    // 只在用户第一次进来的时候判断地址栏的URL
    LaunchedEffect(Unit) {
        val currentUrl = extractUrl(window.location.href)
        if (currentUrl != null && appNavigator.url != currentUrl) {
            logger.info("apply url: ${currentUrl}")
            appNavigator.url = currentUrl
        }
    }

    // 将地址栏URL替换为当前页面URL
    LaunchedEffect(appNavigator.url) {
        val appUrl = appNavigator.url
        val currentUrl = extractUrl(window.location.href)
        if (appUrl != null && currentUrl != appUrl) {
            logger.info("replace url: ${appNavigator.url}")
            window.history.replaceState(null, "", "#/${appNavigator.url}")
        }
    }
}

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

        App(typography = getAppTypography()) {
            val appNavigator = LocalAppNavigator.currentOrThrow

            UrlHandler(appNavigator)
        }

        LaunchedEffect(Unit) {
            document.getElementById("loading-hint")?.remove()
        }
    }
}