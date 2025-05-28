package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class AppBarState(
    val title: String = "",
    val actions: @Composable RowScope.() -> Unit = {}
) {
    companion object {
        val NONE = AppBarState()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    stateList: List<AppBarState?>,
    navigationIcon: (@Composable () -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                stateList.findLast { it != null }?.title ?: ""
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
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