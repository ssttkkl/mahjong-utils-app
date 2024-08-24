package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHost
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.launch


@Composable
fun NavigationItems(
    navigator: AppNavigator,
    navigatableScreens: List<String>,
    drawerState: DrawerState? = null
) {
    val coroutineScope = rememberCoroutineScope()

    navigatableScreens.map { navigator.screenRegistry[it]?.invoke() }.forEach { screen ->
        checkNotNull(screen)

        NavigationDrawerItem(
            label = { Text(screen.navigationTitle) },
            selected = navigator.voyager.lastItem == screen,
            onClick = {
                navigator.voyager.replaceAll(screen)
                coroutineScope.launch {
                    drawerState?.close()
                }
            }
        )
    }
}


@Composable
fun InnerScaffold(
    appState: AppState,
    navigationIcon: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            AppBar(appState.appBarStateList, navigationIcon)
        },
        snackbarHost = {
            SnackbarHost(appState.snackbarHostState)
        }
    ) { innerPadding ->
        AppDialog(appState.appDialogState) {
            appState.appDialogState = AppDialogState.NONE
        }

        Surface(Modifier.padding(innerPadding)) {
            CurrentScreen()
        }

        AppBottomSheet(appState.appBottomSheetState) {
            appState.appBottomSheetState = AppBottomSheetState.NONE
        }
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppScaffold(
    screenRegistry: Map<String, () -> UrlNavigationScreen<*>>,
    navigatableScreens: List<String>,
    initialScreenPath: String = navigatableScreens.first(),
    navigationIcon: @Composable () -> Unit
) {
    AppNavigator(screenRegistry, initialScreenPath) { myNavigator ->
        val windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
        val appState = rememberAppState(
            myNavigator,
            windowSizeClass = windowSizeClass
        )
        CompositionLocalProvider(
            LocalAppState provides appState
        ) {
            TileImeHost {
                if (!appState.useNavigationDrawer) {
                    with(Spacing.current) {
                        Row {
                            val menuScrollState = rememberScrollState()
                            ScrollBox(menuScrollState) {
                                Column(Modifier.width(200.dp).verticalScroll(menuScrollState)) {
                                    NavigationItems(appState.navigator, navigatableScreens)
                                }
                            }

                            Spacer(Modifier.width(panesHorizontalSpacing))

                            Column(Modifier.weight(1f)) {
                                InnerScaffold(appState, navigationIcon)
                            }
                        }
                    }
                } else {
                    ModalNavigationDrawer(
                        gesturesEnabled = !appState.navigator.voyager.canPop,
                        drawerState = appState.drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                val menuScrollState = rememberScrollState()
                                ScrollBox(menuScrollState) {
                                    Column(Modifier.verticalScroll(menuScrollState)) {
                                        IconButton(onClick = {
                                            appState.coroutineScope.launch {
                                                appState.drawerState.close()
                                            }
                                        }) {
                                            Icon(Icons.Default.Close, "")
                                        }
                                        NavigationItems(
                                            appState.navigator,
                                            navigatableScreens,
                                            appState.drawerState
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        InnerScaffold(appState, navigationIcon)
                    }
                }
            }
        }
    }
}