package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppScaffold
import io.ssttkkl.mahjongutils.app.components.appscaffold.rememberAppState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHost
import io.ssttkkl.mahjongutils.app.screens.furoshanten.FuroShantenScreen
import io.ssttkkl.mahjongutils.app.screens.shanten.ShantenScreen
import kotlinx.coroutines.launch

private val navigatableScreens = listOf(ShantenScreen, FuroShantenScreen)

@Composable
fun App() {
    MaterialTheme {
        TileImeHost {
            Navigator(ShantenScreen) { navigator ->
                val appState = rememberAppState(navigator)

                AppScaffold(
                    appState,
                    navigatableScreens,
                    navigationIcon = { canGoBack ->
                        if (!canGoBack) {
                            Icon(Icons.Filled.Menu, "", Modifier.clickable {
                                appState.coroutineScope.launch {
                                    if (appState.drawerState.isClosed) {
                                        appState.drawerState.open()
                                    } else {
                                        appState.drawerState.close()
                                    }
                                }
                            })
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
}
