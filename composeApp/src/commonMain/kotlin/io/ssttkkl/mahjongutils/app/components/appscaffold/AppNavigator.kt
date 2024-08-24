package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator

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

            val url = myNavigator.url

            @Suppress("UNCHECKED_CAST")
            val pathScreen = screenRegistry[url?.path]?.invoke()
                    as? UrlNavigationScreen<UrlNavigationScreenModel>
            val pathScreenModel = pathScreen?.rememberScreenModel()

            LaunchedEffect(myNavigator.voyager, url, pathScreen) {
                if (pathScreen != null && myNavigator.voyager.lastItemOrNull != pathScreen) {
                    if (pathScreenModel != null && url != null) {
                        pathScreen.applyScreenParams(pathScreenModel, url.params)
                    }
                    myNavigator.voyager.replaceAll(pathScreen)
                }
            }

            @Suppress("UNCHECKED_CAST")
            val curScreen = myNavigator.voyager.lastItemOrNull
                    as? UrlNavigationScreen<UrlNavigationScreenModel>
            val curScreenPath = curScreen?.path
            val curScreenParams = curScreen?.rememberScreenParams()
            LaunchedEffect(curScreenPath, curScreenParams) {
                if (curScreenPath != null && curScreenParams != null) {
                    myNavigator.url = Url(
                        curScreenPath,
                        curScreenParams
                    )
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