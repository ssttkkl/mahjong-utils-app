package io.ssttkkl.mahjongutils.app.components.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.AppState
import io.ssttkkl.mahjongutils.app.screens.furoshanten.RouteFuroShanten
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppDrawer(appState: AppState) {
    Column {
        listOf(RouteShanten, RouteFuroShanten).forEach {
            ListItem(Modifier.clickable {
                appState.navigator.navigate(it.route, NavOptions(popUpTo = PopUpTo.First(true)))
                appState.coroutineScope.launch {
                    appState.scaffoldState.drawerState.close()
                }
            }) {
                Text(it.title)
            }
        }
    }
}