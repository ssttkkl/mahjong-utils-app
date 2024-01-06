package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppScaffold
import io.ssttkkl.mahjongutils.app.components.appscaffold.rememberAppState
import io.ssttkkl.mahjongutils.app.screens.about.AboutScreen
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen
import io.ssttkkl.mahjongutils.app.screens.furoshanten.FuroShantenScreen
import io.ssttkkl.mahjongutils.app.screens.hanhu.HanhuScreen
import io.ssttkkl.mahjongutils.app.screens.hora.HoraScreen
import io.ssttkkl.mahjongutils.app.screens.shanten.ShantenScreen
import kotlinx.coroutines.launch

private val navigatableScreens: List<NavigationScreen> = listOf(
    ShantenScreen,
    FuroShantenScreen,
    HoraScreen,
    HanhuScreen,
    AboutScreen
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() {
    MaterialTheme {
        Navigator(ShantenScreen) { navigator ->
            val windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
            val useNavigationDrawer =
                !(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                        && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact)

            val appState = rememberAppState(
                navigator,
                windowSizeClass = windowSizeClass
            )

            AppScaffold(
                appState,
                navigatableScreens,
                useNavigationDrawer,
                navigationIcon = { canGoBack ->
                    if (!canGoBack) {
                        if (useNavigationDrawer) {
                            Icon(Icons.Filled.Menu, "", Modifier.clickable {
                                appState.coroutineScope.launch {
                                    if (appState.drawerState.isClosed) {
                                        appState.drawerState.open()
                                    } else {
                                        appState.drawerState.close()
                                    }
                                }
                            })
                        }
                    } else {
                        Icon(Icons.Filled.ArrowBack, "", Modifier.clickable {
                            appState.navigator.pop()
                        })
                    }
                }
            )
        }
    }
}
