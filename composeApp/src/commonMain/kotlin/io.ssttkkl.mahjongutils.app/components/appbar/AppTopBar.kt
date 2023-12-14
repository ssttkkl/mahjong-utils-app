package io.ssttkkl.mahjongutils.app.components.appbar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.ssttkkl.mahjongutils.app.AppState
import io.ssttkkl.mahjongutils.app.screens.RouteInfoContainer
import kotlinx.coroutines.launch

@Composable
fun AppTopBar(appState: AppState) {
    val currentEntry by appState.navigator.currentEntry.collectAsState(null)
    val canGoBack by appState.navigator.canGoBack.collectAsState(false)
    TopAppBar(
        title = {
            RouteInfoContainer[currentEntry?.route?.route]?.title?.let {
                Text(it)
            }
        },
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = {
                    appState.navigator.goBack()
                }) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            } else {
                IconButton(onClick = {
                    appState.coroutineScope.launch {
                        appState.scaffoldState.drawerState.open()
                    }
                }) {
                    Icon(Icons.Filled.Menu, "Menu")
                }
            }
        }
    )
}