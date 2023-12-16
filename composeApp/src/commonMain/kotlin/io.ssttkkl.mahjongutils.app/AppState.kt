package io.ssttkkl.mahjongutils.app

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import kotlinx.coroutines.CoroutineScope
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator

@Stable
class AppState(
    val scaffoldState: ScaffoldState,
    val coroutineScope: CoroutineScope,
    val navigator: Navigator,
    val tileImeHostState: TileImeHostState
)

@Composable
fun rememberAppState(): AppState {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val navigator = rememberNavigator()
    val tileImeHostState = remember { TileImeHostState() }

    return remember(scaffoldState, coroutineScope, navigator, tileImeHostState) {
        AppState(scaffoldState, coroutineScope, navigator, tileImeHostState)
    }
}

val LocalAppState =
    compositionLocalOf<AppState> { error("Cannot get LocalNavigator outside of App") }