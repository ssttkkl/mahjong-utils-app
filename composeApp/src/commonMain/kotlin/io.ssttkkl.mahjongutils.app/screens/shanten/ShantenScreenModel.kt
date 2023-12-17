package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import io.ssttkkl.mahjongutils.app.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import mahjongutils.models.Tile

class ShantenScreenModel : ScreenModel {
    var tiles = MutableStateFlow<List<Tile>>(emptyList())
    var shantenMode = MutableStateFlow(ShantenMode.Union)

    fun onSubmit(appState: AppState, snackbarHostState: SnackbarHostState) {
        val args = ShantenArgs(tiles.value, shantenMode.value)
        val navigator = appState.subPaneNavigator ?: appState.mainPaneNavigator
        navigator.push(ShantenResultScreen(args))
    }
}