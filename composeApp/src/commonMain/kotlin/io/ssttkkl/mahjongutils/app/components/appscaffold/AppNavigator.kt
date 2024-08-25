package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory

@Stable
class AppNavigator(
    val voyager: Navigator,
    val screenRegistry: Map<String, () -> UrlNavigationScreen<*>>
) {
    var url by mutableStateOf<Url?>(null)
}

typealias AppNavigatorContent = @Composable (navigator: AppNavigator) -> Unit

val LocalAppNavigator: ProvidableCompositionLocal<AppNavigator?> =
    staticCompositionLocalOf { null }

@Composable
fun AppNavigator(
    screenRegistry: Map<String, () -> UrlNavigationScreen<*>>,
    initialScreenPath: String,
    content: AppNavigatorContent = { CurrentScreen() }
) {
    val logger = remember { LoggerFactory.getLogger("AppNavigator") }

    val initialScreen = screenRegistry[initialScreenPath]
    checkNotNull(initialScreen)
    Navigator(initialScreen()) {
        val myNavigator = remember(it, screenRegistry) {
            AppNavigator(it, screenRegistry)
        }

        CompositionLocalProvider(
            LocalAppNavigator provides myNavigator
        ) {
            content(myNavigator)

            // ==== TODO: 写得很屎但是不知道怎么改了 ====
            val url = myNavigator.url
            var jumpingScreen: UrlNavigationScreen<*>? by remember { mutableStateOf(null) }

            // 如果url有更新，跳转到url指定页面
            @Suppress("UNCHECKED_CAST")
            val pathScreen = remember(screenRegistry, url?.path) {
                screenRegistry[url?.path]?.invoke()
                        as? UrlNavigationScreen<UrlNavigationScreenModel>
            }
            val pathScreenModel = pathScreen?.rememberScreenModel()
            val pathParams = pathScreen?.rememberScreenParams()
            LaunchedEffect(url) {
                // 页面不同，跳转到该页面并应用参数
                if (pathScreen != null && myNavigator.voyager.lastItemOrNull != pathScreen) {
                    if (pathScreenModel != null && url != null) {
                        logger.info("apply screen params: $pathScreenModel ${url.params}")
                        pathScreen.applyScreenParams(pathScreenModel, url.params)
                    }
                    logger.info("jump to screen: $pathScreen")
                    myNavigator.voyager.replaceAll(pathScreen)
                    jumpingScreen = pathScreen
                } else {
                    // 页面相同但参数不同，应用参数
                    if (pathScreenModel != null && url != null && pathParams != url.params) {
                        logger.info("apply screen params: $pathScreenModel ${url.params}")
                        pathScreen.applyScreenParams(pathScreenModel, url.params)
                    }
                }
            }

            // 如果有路由跳转，更新url
            @Suppress("UNCHECKED_CAST")
            val curScreen = myNavigator.voyager.lastItemOrNull
                    as? UrlNavigationScreen<UrlNavigationScreenModel>
            val curScreenPath = curScreen?.path
            val curScreenParams = curScreen?.rememberScreenParams()
            LaunchedEffect(curScreenPath, curScreenParams, jumpingScreen) {
                // 如果上一次url更新引发的路由跳转还未生效，直接跳出
                if (jumpingScreen != null && curScreen != jumpingScreen) {
                    return@LaunchedEffect
                }
                jumpingScreen = null

                if (curScreenPath != null && curScreenParams != null) {
                    myNavigator.url = Url(
                        curScreenPath,
                        curScreenParams
                    )
                    logger.info("update url: ${myNavigator.url}")
                }
            }
        }
    }
}

val Navigator.rootNavigator: Navigator
    get() {
        var cur = this
        while (cur.parent != null) {
            cur = cur.parent!!
        }
        return cur
    }