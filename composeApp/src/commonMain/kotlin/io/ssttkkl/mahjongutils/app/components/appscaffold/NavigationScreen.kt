package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import org.jetbrains.compose.resources.StringResource

abstract class NavigationScreen : Screen {
    abstract val title: StringResource?

    @Composable
    open fun RowScope.TopBarActions(appState: AppState) {
    }
}

