package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory

@Stable
class AppState(
    val navigator: AppNavigator,
    val density: Density,
    val captureController: CaptureController,
    val extra: Map<String, Any?> = emptyMap()
) {
    companion object {
        val logger = LoggerFactory.getLogger(AppState::class)
    }

    val snackbarHostState = SnackbarHostState()
    var appDialogState by mutableStateOf(AppDialogState.NONE)
    var appBottomSheetState by mutableStateOf(AppBottomSheetState(density))

    // 因为navigator能够嵌套
    val appBarStateList = mutableStateListOf<AppBarState?>()

    fun setAppBarState(appBarState: AppBarState, level: Int) {
        if (appBarStateList.getOrNull(level) != appBarState) {
            while (appBarStateList.size <= level) {
                appBarStateList.add(null)
            }
            appBarStateList[level] = appBarState
        }
    }

    fun concernAppBarLevel(level: Int) {
        while (appBarStateList.size > level + 1) {
            appBarStateList.removeAt(appBarStateList.size - 1)
        }
        while (appBarStateList.size < level) {
            appBarStateList.add(null)
        }
    }
}

@Composable
fun rememberAppState(
    navigator: AppNavigator,
    density: Density = LocalDensity.current,
    captureController: CaptureController = rememberCaptureController(),
    extra: Map<String, Any?> = rememberAppStateExtra()
): AppState {
    return remember(navigator, density, captureController, extra) {
        AppState(
            navigator = navigator,
            density = density,
            captureController = captureController,
            extra = extra
        )
    }
}

@Composable
expect fun rememberAppStateExtra(): Map<String, Any?>

val LocalAppState = compositionLocalOf<AppState> {
    error("No LocalAppState provided! ")
}
