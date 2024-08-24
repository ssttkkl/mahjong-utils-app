package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import cafe.adriel.voyager.navigator.currentOrThrow

class NavigationScreenState : ScreenModel {
    var nestedNavigator: Navigator? by mutableStateOf(null)
}

abstract class NavigationScreen : Screen {
    companion object {
        @Composable
        private fun rememberNavigationScreenState(
            key: String,
            navigator: Navigator = LocalNavigator.currentOrThrow
        ): NavigationScreenState {
            val model = navigator.rememberNavigatorScreenModel(key) {
                NavigationScreenState()
            }
            return model
        }
    }

    val screenState: NavigationScreenState
        @Composable
        get() {
            var currentNavigator: Navigator? = LocalNavigator.currentOrThrow
            while (currentNavigator != null && !currentNavigator.items.contains(this)) {
                currentNavigator = currentNavigator.parent
            }
            if (currentNavigator != null) {
                return rememberNavigationScreenState(key, currentNavigator)
            } else {
                error("this screen not contains in current navigator")
            }
        }

    open val title: String
        @Composable
        get() = ""

    open val navigationTitle: String
        @Composable
        get() = title

    @Composable
    open fun RowScope.TopBarActions() {
    }

    open val overrideNavigationIcon: Boolean
        @Composable
        get() = screenState.nestedNavigator?.canPop == true

    @Composable
    open fun NavigationIcon() {
        val curNestedNavigator by rememberUpdatedState(screenState.nestedNavigator)
        Icon(Icons.AutoMirrored.Default.ArrowBack, "", Modifier.clickable {
            curNestedNavigator?.pop()
        })
    }

    @Composable
    final override fun Content() {
        val curTitle by rememberUpdatedState(title)
        val curOverrideNavigationIcon by rememberUpdatedState(overrideNavigationIcon)
        val curNavigator by rememberUpdatedState(LocalNavigator.currentOrThrow)
        val screenAppBarState = remember(curTitle, curOverrideNavigationIcon) {
            AppBarState(
                title = curTitle,
                actions = {
                    CompositionLocalProvider(LocalNavigator provides curNavigator) {
                        TopBarActions()
                    }
                },
                overrideNavigationIcon = if (curOverrideNavigationIcon) {
                    {
                        CompositionLocalProvider(LocalNavigator provides curNavigator) {
                            NavigationIcon()
                        }
                    }
                } else {
                    null
                }
            )
        }

        // 替换掉appState.appBarStateList第level级的元素
        val appBarStateList = LocalAppState.current.appBarStateList
        val navigator = LocalNavigator.currentOrThrow
        if (appBarStateList.getOrNull(navigator.level) != screenAppBarState) {
            while (appBarStateList.size <= navigator.level) {
                appBarStateList.add(null)
            }
            appBarStateList[navigator.level] = screenAppBarState
        }

        ScreenContent()
    }

    @Composable
    fun NestedNavigator(
        screen: Screen,
        content: NavigatorContent
    ) {
        val screenState = screenState
        Navigator(screen) { nestedNavigator ->
            screenState.nestedNavigator = nestedNavigator
            content(nestedNavigator)
        }
    }

    @Composable
    abstract fun ScreenContent()
}

abstract class UrlNavigationScreenModel : ScreenModel {
    fun extractParams(): List<Pair<String, String>> {
        return emptyList()
    }

    fun applyParams(params: List<Pair<String, String>>) {}
}

abstract class UrlNavigationScreen<M : UrlNavigationScreenModel> : NavigationScreen() {
    abstract val path: String

    override val key: ScreenKey
        get() = path

    @Composable
    abstract fun getScreenModel(): M
}

object VoidNavigationScreenModel : UrlNavigationScreenModel()

abstract class NoParamUrlNavigationScreen : UrlNavigationScreen<VoidNavigationScreenModel>() {
    @Composable
    override fun getScreenModel(): VoidNavigationScreenModel {
        return VoidNavigationScreenModel
    }
}
