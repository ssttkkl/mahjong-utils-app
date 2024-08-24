package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator

class AppNavigator(
    val voyager: Navigator,
    val screenRegistry: Map<String, () -> UrlNavigationScreen<*>>,
)

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
        val myNavigator = remember(it) {
            AppNavigator(it, screenRegistry)
        }

        CompositionLocalProvider(
            LocalAppNavigator provides myNavigator
        ) {
            content(myNavigator)
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