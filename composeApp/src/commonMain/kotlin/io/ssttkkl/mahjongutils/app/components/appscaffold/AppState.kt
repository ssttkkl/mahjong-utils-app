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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope

@Stable
class AppState(
    val coroutineScope: CoroutineScope,
    val navigator: AppNavigator,
    val windowSizeClass: WindowSizeClass,
    val density: Density,
) {
    val drawerState = DrawerState(DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()

    // 因为navigator能够嵌套
    val appBarStateList = mutableStateListOf<AppBarState?>()
    var appDialogState by mutableStateOf(AppDialogState.NONE)
    var appBottomSheetState by mutableStateOf(AppBottomSheetState(density))

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
    density: Density = LocalDensity.current
): AppState {
    return remember(navigator, coroutineScope, windowSizeClass, density) {
        AppState(
            navigator = navigator,
            coroutineScope = coroutineScope,
            windowSizeClass = windowSizeClass,
            density = density
        )
    }
}

val LocalAppState = compositionLocalOf<AppState> {
    error("No LocalAppState provided! ")
}
