package io.ssttkkl.mahjongutils.app.components.basepane

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.navigator.NavigationScreen

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No LocalSnackbarHostState provided! ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePane(
    navigator: Navigator,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationIcon: @Composable (canGoBack: Boolean) -> Unit
) {
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            topBar = {
                TopAppBar(
                    title = { Text((navigator.lastItem as? NavigationScreen)?.title ?: "") },
                    navigationIcon = {
                        navigationIcon(navigator.canPop)
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                CurrentScreen()
            }
        }
    }
}