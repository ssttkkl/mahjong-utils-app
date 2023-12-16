package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appbar.AppTopBar
import io.ssttkkl.mahjongutils.app.components.drawer.AppDrawer
import io.ssttkkl.mahjongutils.app.components.navigator.scene
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHost
import io.ssttkkl.mahjongutils.app.screens.RouteInfoContainer
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost

@Composable
fun App() {
    PreComposeApp {
        val appState = rememberAppState()
        CompositionLocalProvider(
            LocalAppState provides appState,
        ) {
            TileImeHost(appState.tileImeHostState) {
                AppContent(appState)
            }
        }
    }
}

@Composable
fun AppContent(appState: AppState) {
    MaterialTheme {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            scaffoldState = appState.scaffoldState,
            topBar = {
                AppTopBar(appState)
            },
            drawerContent = {
                AppDrawer(appState)
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    modifier = Modifier.systemBarsPadding(),
                    snackbar = { snackbarData -> Snackbar(snackbarData) }
                )
            },
        ) {
            NavHost(
                navigator = appState.navigator,
                initialRoute = RouteShanten.route
            ) {
                RouteInfoContainer.values.forEach {
                    scene(it)
                }
            }
        }
    }
}