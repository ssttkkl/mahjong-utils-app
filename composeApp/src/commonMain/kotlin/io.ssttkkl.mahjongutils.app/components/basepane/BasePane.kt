package io.ssttkkl.mahjongutils.app.components.basepane

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.navigator.RouteInfo
import io.ssttkkl.mahjongutils.app.components.navigator.scene
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator

val LocalNavigator = compositionLocalOf<Navigator> {
    error("No LocalNavigator provided! ")
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No LocalSnackbarHostState provided! ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePane(
    routes: Map<String, RouteInfo>,
    initialRoute: RouteInfo,
    navigator: Navigator = rememberNavigator(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationIcon: @Composable (canGoBack: Boolean) -> Unit
) {
    val currentEntry by navigator.currentEntry.collectAsState(null)
    val canGoBack by navigator.canGoBack.collectAsState(false)

    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            topBar = {
                TopAppBar(
                    title = { Text(routes[currentEntry?.route?.route]?.title ?: "") },
                    navigationIcon = {
                        navigationIcon(canGoBack)
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navigator = navigator,
                initialRoute = initialRoute.route
            ) {
                routes.values.forEach {
                    scene(it)
                }
            }
        }
    }
}