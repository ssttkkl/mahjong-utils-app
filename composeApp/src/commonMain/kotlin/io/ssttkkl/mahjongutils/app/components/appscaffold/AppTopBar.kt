package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable


data class AppBarState(
    val title: String = "",
    val actions: @Composable RowScope.() -> Unit = {},
    val overrideNavigationIcon: (@Composable () -> Unit)? = null
) {
    companion object {
        val NONE = AppBarState()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    stateList: List<AppBarState?>,
    navigationIcon: @Composable () -> Unit
) {
    TopAppBar(
        title = {
            val lastNotNull = stateList.findLast { it != null } ?: AppBarState.NONE
            Text(
                lastNotNull.title
            )
        },
        navigationIcon = {
            val lastOverride =
                stateList.findLast { it?.overrideNavigationIcon != null }?.overrideNavigationIcon
            (lastOverride ?: navigationIcon)()
        },
        actions = {
            stateList.forEach {
                it?.let { state ->
                    with(state) {
                        actions()
                    }
                }
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}