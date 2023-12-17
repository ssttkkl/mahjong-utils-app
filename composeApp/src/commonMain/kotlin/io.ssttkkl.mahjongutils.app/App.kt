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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.basepane.BasePane
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHost
import io.ssttkkl.mahjongutils.app.screens.furoshanten.FuroShantenScreen
import io.ssttkkl.mahjongutils.app.screens.shanten.ShantenScreen
import kotlinx.coroutines.launch

private val navigatableScreens = listOf(ShantenScreen, FuroShantenScreen)

@Composable
fun App() {
    MaterialTheme {
        TileImeHost {
            AppContent()
        }
    }
}

@Composable
fun CompactAppContent() {
    Navigator(ShantenScreen) { navigator ->
        val appState = rememberAppState(navigator, null)
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        CompositionLocalProvider(LocalAppState provides appState) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        navigatableScreens.forEach {
                            NavigationDrawerItem(
                                label = { Text(text = it.title) },
                                selected = navigator.lastItem == it,
                                onClick = {
                                    navigator.replaceAll(it)
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
                                navigator.pop()
                            })
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppContent() {
    val windowSizeClass = calculateWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> CompactAppContent()
    }
}
