package io.ssttkkl.mahjongutils.app.components.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.AppState
import io.ssttkkl.mahjongutils.app.screens.furoshanten.RouteFuroShanten
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppDrawer(appState: AppState) {
    val currentEntry by appState.navigator.currentEntry.collectAsState(null)
    Column(Modifier.selectableGroup()) {
        Spacer(Modifier.height(Spacing.medium))

        listOf(RouteShanten, RouteFuroShanten).forEach {
            ListItem(Modifier.selectable(currentEntry?.route?.route == it.route) {
                appState.navigator.navigate(it.route, NavOptions(popUpTo = PopUpTo.First(true)))
                appState.coroutineScope.launch {
                    appState.scaffoldState.drawerState.close()
                }
            }) {
                Text(it.title)
            }
        }

        Spacer(Modifier.height(Spacing.medium))
    }
}