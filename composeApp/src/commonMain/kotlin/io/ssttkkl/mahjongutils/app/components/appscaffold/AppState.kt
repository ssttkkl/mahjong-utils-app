package io.ssttkkl.mahjongutils.app.components.appscaffold

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope


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

class AppDialogState(
    val dialog: @Composable (onDismissRequest: () -> Unit) -> Unit
) {
    var visible: Boolean by mutableStateOf(false)

    companion object {
        val NONE = AppDialogState {}
    }
}

class AppState(
    val coroutineScope: CoroutineScope,
    val navigator: Navigator,
    val windowSizeClass: WindowSizeClass,
) {
    val drawerState = DrawerState(DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()

    var appDialogState by mutableStateOf(AppDialogState.NONE)
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
