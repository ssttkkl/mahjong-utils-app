package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.basepane.BasePane
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHost
import io.ssttkkl.mahjongutils.app.screens.AllRouteInfo
import io.ssttkkl.mahjongutils.app.screens.NavigationRouteInfo
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

@Composable
fun App() {
    PreComposeApp {
        MaterialTheme {
            TileImeHost {
                val appState = rememberAppState()
                CompositionLocalProvider(LocalAppState provides appState) {
                    AppContent(appState)
                }
            }
        }
    }
}

@Composable
fun CompactAppContent(appState: AppState) {
    val coroutineScope = rememberCoroutineScope()
    val currentEntry by appState.mainPaneNavigator.currentEntry.collectAsState(null)
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationRouteInfo.values.forEach {
                    NavigationDrawerItem(
                        label = { Text(text = it.title) },
                        selected = currentEntry?.route?.route == it.route,
                        onClick = {
                            appState.mainPaneNavigator.navigate(
                                it.route,
                                NavOptions(popUpTo = PopUpTo.First(true))
                            )
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            }
        }
    ) {
        BasePane(
            AllRouteInfo,
            RouteShanten,
            appState.mainPaneNavigator,
            navigationIcon = { canGoBack ->
                if (!canGoBack) {
                    Icon(Icons.Filled.Menu, "", Modifier.clickable {
                        coroutineScope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    })
                } else {
                    Icon(Icons.Filled.ArrowBack, "", Modifier.clickable {
                        appState.mainPaneNavigator.goBack()
                    })
                }
            }
        )
    }
}

@Composable
fun AppContent(appState: AppState) {
    when (appState.windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> CompactAppContent(appState)
    }
}
