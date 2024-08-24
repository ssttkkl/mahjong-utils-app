package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope


class AppState(
    val coroutineScope: CoroutineScope,
    val navigator: AppNavigator,
    val windowSizeClass: WindowSizeClass,
) {
    val drawerState = DrawerState(DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()

    // 因为navigator能够嵌套
    val appBarStateList = mutableStateListOf<AppBarState?>()
    var appDialogState by mutableStateOf(AppDialogState.NONE)
    var appBottomSheetState by mutableStateOf(AppBottomSheetState.NONE)

    val useNavigationDrawer: Boolean
        get() = !(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact)

}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppState(
    navigator: AppNavigator,
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
