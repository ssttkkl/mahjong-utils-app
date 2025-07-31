package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState

@Composable
actual fun TileRecognizerHost(
    appState: AppState,
    content: @Composable () -> Unit
) {
    DefaultTileRecognizerHost(appState, content)
}