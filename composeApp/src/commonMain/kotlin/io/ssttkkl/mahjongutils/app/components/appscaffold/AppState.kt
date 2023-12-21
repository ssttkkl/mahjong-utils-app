package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope

class AppBarState {
    private val actionsProvider: MutableMap<Any?, ActionsProvider> = mutableStateMapOf()

    data class ActionsProvider(
        val key: Any?,
        val priority: Int = 50,
        val actions: @Composable RowScope.() -> Unit,
    )

    @Composable
    fun RowScope.Actions() {
        val providers by derivedStateOf { actionsProvider.values.sortedBy { it.priority } }
        providers.forEach {
            it.actions(this)
        }
    }

    fun addActionsProvider(
        key: Any?,
        priority: Int = 50,
        actions: @Composable RowScope.() -> Unit
    ) {
        actionsProvider[key] = ActionsProvider(key, priority, actions)
    }

    fun removeActionsProvider(key: Any?) {
        actionsProvider.remove(key)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class AppBottomSheetState(
    val content: @Composable () -> Unit
) {
    var visible: Boolean by mutableStateOf(false)
    val sheetState: SheetState = SheetState(false)

    companion object {
        val NONE = AppBottomSheetState {}
    }
}

class AppState(
    val coroutineScope: CoroutineScope,
    val navigator: Navigator,
    val windowSizeClass: WindowSizeClass,
) {
    val drawerState = DrawerState(DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()
    var appBarState = AppBarState()

    var appBottomSheetState by mutableStateOf(AppBottomSheetState.NONE)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppState(
    navigator: Navigator,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
): AppState {
    return remember(navigator, coroutineScope, windowSizeClass) {
        AppState(
            navigator = navigator,
            coroutineScope = coroutineScope,
            windowSizeClass = windowSizeClass
        )
    }
}

val LocalAppState = compositionLocalOf<AppState> {
    error("No LocalAppState provided! ")
}
