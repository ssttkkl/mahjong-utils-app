package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import io.ssttkkl.mahjongutils.app.base.Spacing
import io.ssttkkl.mahjongutils.app.base.components.ScrollBox
import io.ssttkkl.mahjongutils.app.base.rememberWindowSizeClass
import io.ssttkkl.mahjongutils.app.components.tile.TileRecognizerHost
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHost
import kotlinx.coroutines.launch


val WindowSizeClass.useNavigationDrawer: Boolean
    get() = !(widthSizeClass == WindowWidthSizeClass.Expanded
            && heightSizeClass != WindowHeightSizeClass.Compact)

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
            selected = navigator.rootVoyager.lastItem == screen,
            onClick = {
                navigator.rootVoyager.replaceAll(screen)
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
    additionalContent: @Composable () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = null,
) {
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            .safeDrawingPadding(),
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
            additionalContent()
        }

        val density = LocalDensity.current
        AppBottomSheet(appState.appBottomSheetState) {
            appState.appBottomSheetState = AppBottomSheetState(density, {})
        }
    }
}


@Composable
fun AppScaffold(
    screenRegistry: Map<String, () -> UrlNavigationScreen<*>>,
    navigatableScreens: List<String>,
    initialScreenPath: String = navigatableScreens.first(),
    additionalContent: @Composable () -> Unit = {}
) {
    AppNavigator(screenRegistry, initialScreenPath) { myNavigator ->
        val windowSizeClass: WindowSizeClass = rememberWindowSizeClass()
        val appState = rememberAppState(myNavigator)
        CompositionLocalProvider(
            LocalAppState provides appState
        ) {
            TileRecognizerHost(appState) {
                TileImeHost {
                    if (!windowSizeClass.useNavigationDrawer) {
                        with(Spacing.current) {
                            Row(Modifier.background(MaterialTheme.colorScheme.surface)) {
                                val menuScrollState = rememberScrollState()
                                ScrollBox(menuScrollState) {
                                    Column(Modifier.width(200.dp).verticalScroll(menuScrollState)) {
                                        NavigationItems(appState.navigator, navigatableScreens)
                                    }
                                }

                                Spacer(Modifier.width(panesHorizontalSpacing))

                                Column(Modifier.weight(1f)) {
                                    InnerScaffold(appState, additionalContent)
                                }
                            }
                        }
                    } else {
                        val drawerState = rememberDrawerState(DrawerValue.Closed)
                        val coroutineScope = rememberCoroutineScope()

                        val navigationIcon = @Composable {
                            if (!appState.navigator.canPop) {
                                if (windowSizeClass.useNavigationDrawer) {
                                    Icon(Icons.Default.Menu, "Menu", Modifier.clickable {
                                        coroutineScope.launch {
                                            if (drawerState.isClosed) {
                                                drawerState.open()
                                            } else {
                                                drawerState.close()
                                            }
                                        }
                                    })
                                }
                            } else {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    "Back",
                                    Modifier.clickable {
                                    appState.navigator.pop()
                                })
                            }
                        }

                        ModalNavigationDrawer(
                            gesturesEnabled = !appState.navigator.canPop,
                            drawerState = drawerState,
                            drawerContent = {
                                ModalDrawerSheet {
                                    val menuScrollState = rememberScrollState()
                                    ScrollBox(menuScrollState) {
                                        Column(Modifier.verticalScroll(menuScrollState)) {
                                            IconButton(onClick = {
                                                coroutineScope.launch {
                                                    drawerState.close()
                                                }
                                            }) {
                                                Icon(Icons.Default.Close, "Close")
                                            }
                                            NavigationItems(
                                                appState.navigator,
                                                navigatableScreens,
                                                drawerState
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            InnerScaffold(appState, additionalContent, navigationIcon)
                        }
                    }
                }
            }
        }
    }
}
