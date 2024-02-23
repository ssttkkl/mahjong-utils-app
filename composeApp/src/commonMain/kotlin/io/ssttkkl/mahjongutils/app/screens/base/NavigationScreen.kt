package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState

abstract class NavigationScreen : Screen {
    abstract val title: StringResource?

    @Composable
    open fun RowScope.TopBarActions(appState: AppState) {
    }

    @Composable
    override fun Content() {
        val appState = LocalAppState.current
        LifecycleEffect(
            onStarted = {
                appState.appBarState.addActionsProvider(this) {
                    TopBarActions(appState)
                }
            },
            onDisposed = {
                appState.appBarState.removeActionsProvider(this)
            }
        )
    }
}

