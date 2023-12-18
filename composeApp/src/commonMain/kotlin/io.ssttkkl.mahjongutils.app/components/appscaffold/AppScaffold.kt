package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import io.ssttkkl.mahjongutils.app.components.navigator.NavigationScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    appState: AppState,
    navigatableScreens: List<NavigationScreen>,
    navigationIcon: @Composable (canGoBack: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalAppState provides appState
    ) {
        ModalNavigationDrawer(
            drawerState = appState.drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    navigatableScreens.forEach {
                        NavigationDrawerItem(
                            label = { Text(text = it.title) },
                            selected = appState.navigator.lastItem == it,
                            onClick = {
                                appState.navigator.replaceAll(it)
                                coroutineScope.launch {
                                    appState.drawerState.close()
                                }
                            }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.safeDrawingPadding(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                (appState.navigator.lastItem as? NavigationScreen)?.title ?: ""
                            )
                        },
                        navigationIcon = {
                            navigationIcon(appState.navigator.canPop)
                        },
                        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                    )
                },
                snackbarHost = {
                    SnackbarHost(appState.snackbarHostState)
                }
            ) { innerPadding ->
                Box(Modifier.padding(innerPadding)) {
                    CurrentScreen()
                }
            }
        }
    }
}