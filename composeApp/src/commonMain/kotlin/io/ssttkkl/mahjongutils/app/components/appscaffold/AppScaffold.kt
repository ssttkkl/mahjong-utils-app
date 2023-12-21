package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.launch


@Composable
fun ColumnScope.NavigationItems(
    navigator: Navigator,
    navigatableScreens: List<NavigationScreen>,
    drawerState: DrawerState? = null
) {
    val coroutineScope = rememberCoroutineScope()

    navigatableScreens.forEach {
        NavigationDrawerItem(
            label = { Text(stringResource(it.title)) },
            selected = navigator.lastItem == it,
            onClick = {
                navigator.replaceAll(it)
                coroutineScope.launch {
                    drawerState?.close()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBottomSheet(
    state: AppBottomSheetState
) {
    if (state.visible) {
        ModalBottomSheet(
            onDismissRequest = { state.visible = false },
            sheetState = state.sheetState
        ) {
            state.content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    state: AppBarState,
    navigator: Navigator,
    navigationIcon: @Composable (canGoBack: Boolean) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                (navigator.lastItem as? NavigationScreen)
                    ?.title
                    ?.let { stringResource(it) } ?: ""
            )
        },
        navigationIcon = {
            navigationIcon(navigator.canPop)
        },
        actions = {
            with(state) {
                Actions()
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}

@Composable
fun AppScaffold(
    appState: AppState,
    navigatableScreens: List<NavigationScreen>,
    useNavigationDrawer: Boolean,
    navigationIcon: @Composable (canGoBack: Boolean) -> Unit
) {
    @Composable
    fun InnerScaffold() {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            topBar = {
                AppBar(appState.appBarState, appState.navigator, navigationIcon)
            },
            snackbarHost = {
                SnackbarHost(appState.snackbarHostState)
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                CurrentScreen()
            }

            AppBottomSheet(appState.appBottomSheetState)
        }
    }

    CompositionLocalProvider(
        LocalAppState provides appState
    ) {
        if (!useNavigationDrawer) {
            with(Spacing.current) {
                Row {
                    Column(Modifier.width(200.dp)) {
                        NavigationItems(appState.navigator, navigatableScreens)
                    }
                    Spacer(Modifier.width(panesHorizontalSpacing))
                    Column(Modifier.weight(1f)) {
                        InnerScaffold()
                    }
                }
            }
        } else {
            ModalNavigationDrawer(
                drawerState = appState.drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        NavigationItems(
                            appState.navigator,
                            navigatableScreens,
                            appState.drawerState
                        )
                    }
                }
            ) {
                InnerScaffold()
            }
        }
    }
}