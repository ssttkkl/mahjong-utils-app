@file:OptIn(ExperimentalMaterial3Api::class)

package io.ssttkkl.mahjongutils.app.kuikly.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.RowScope
import com.tencent.kuikly.compose.material3.ExperimentalMaterial3Api
import com.tencent.kuikly.compose.material3.MaterialTheme
import com.tencent.kuikly.compose.material3.Scaffold
import com.tencent.kuikly.compose.material3.SnackbarHost
import com.tencent.kuikly.compose.material3.SnackbarHostState
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TopAppBar
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.kuikly.components.icons.Icons
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.DrawerState
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.DrawerValue
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.Icon
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.IconButton
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.ModalDrawerSheet
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.ModalNavigationDrawer
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.NavigationDrawerItem
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.rememberDrawerState
import kotlinx.coroutines.launch

class ScreenState {
    val snackbarHostState = SnackbarHostState()
}

val LocalScreenState = compositionLocalOf<ScreenState> {
    error("only available inside Screen")
}

@Stable
abstract class Screen {
    abstract val path: String

    open val title: String
        @Composable
        get() = ""

    open val navigationTitle: String
        @Composable
        get() = title

    open val canGoBack: Boolean
        get() = false

    open val enableNavigationDrawer: Boolean
        get() = !canGoBack

    @Composable
    open fun RowScope.TopBarActions() {
    }

    @Composable
    abstract fun ScreenContent()

    @Composable
    private fun NavigationItems(
        screens: List<Screen>,
        onRequestCloseDrawer: () -> Unit
    ) {
        val pager = CurrentPager()

        screens.forEach { screen ->
            checkNotNull(screen)

            NavigationDrawerItem(
                label = { Text(screen.navigationTitle) },
                selected = this.path == screen.path,
                onClick = {
                    if (this.path != screen.path) {
                        pager.router().closePage()
                        pager.router().openPage(screen.path)
                    }
                    onRequestCloseDrawer()
                }
            )
        }
    }

    @Composable
    private fun ScreenNavigationDrawer(
        drawerState: DrawerState,
        content: @Composable () -> Unit = {}
    ) {
        val coroutineScope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Close, "Close")
                        }
                        NavigationItems(
                            screens = ScreenRegistry.navigationScreens,
                            onRequestCloseDrawer = {
                                coroutineScope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            }
        ) {
            content()
        }
    }

    @Composable
    protected fun ScreenScaffold(innerContent: @Composable (innerPadding: PaddingValues) -> Unit) {
        val pager = CurrentPager()
        val scope = rememberCoroutineScope()

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val screenState = remember {
            ScreenState()
        }

        @Composable
        fun InnerScaffold() {
            Scaffold(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                topBar = {
                    ScreenTopBar(
                        onRequestGoBack = { pager.router().closePage() },
                        onRequestSwitchDrawer = {
                            scope.launch {
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
                                    drawerState.open()
                                }
                            }
                        }
                    )
                },
                snackbarHost = {
                    SnackbarHost(screenState.snackbarHostState)
                }
            ) { innerPadding ->
                innerContent(innerPadding)
            }
        }

        MaterialTheme {
            CompositionLocalProvider(LocalScreenState provides screenState) {
                if (enableNavigationDrawer) {
                    ScreenNavigationDrawer(drawerState) {
                        InnerScaffold()
                    }
                } else {
                    InnerScaffold()
                }
            }
        }
    }

    @Composable
    protected fun ScreenTopBar(
        onRequestGoBack: () -> Unit,
        onRequestSwitchDrawer: () -> Unit
    ) {
        TopAppBar(
            title = {
                Text(title)
            },
            navigationIcon = {
                if (canGoBack) {
                    Icon(Icons.Default.ArrowBack, "Go Back", Modifier.clickable {
                        onRequestGoBack()
                    })
                } else {
                    Icon(Icons.Default.Menu, "Menu", Modifier.clickable {
                        onRequestSwitchDrawer()
                    })
                }
            },
            actions = {
                TopBarActions()
            }
        )
    }
}


abstract class ScreenPager() : ComposeContainer() {
    abstract fun createScreen(): Screen

    override fun willInit() {
        super.willInit()

        val screen = createScreen()
        setContent {
            screen.ScreenContent()
        }
    }
}