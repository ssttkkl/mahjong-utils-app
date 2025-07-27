package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState

@Composable
actual fun TileRecognizerHost(
    appState: AppState,
    tileImeHostState: TileImeHostState,
    content: @Composable () -> Unit
) {
    DefaultTileRecognizerHost(appState, tileImeHostState, content)
}