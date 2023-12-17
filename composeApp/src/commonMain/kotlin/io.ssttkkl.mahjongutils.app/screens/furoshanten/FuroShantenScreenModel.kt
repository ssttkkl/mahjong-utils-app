package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import io.ssttkkl.mahjongutils.app.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import mahjongutils.models.Tile

class FuroShantenScreenModel : ScreenModel {
    val tiles = MutableStateFlow<List<Tile>>(emptyList())
    val chanceTile = MutableStateFlow<Tile?>(null)
    val allowChi = MutableStateFlow(false)

    fun onSubmit(appState: AppState, snackbarHostState: SnackbarHostState) {
        val chanceTile = chanceTile.value ?: return
        val args = FuroChanceShantenArgs(tiles.value, chanceTile, allowChi.value)
        val navigator = appState.subPaneNavigator ?: appState.mainPaneNavigator
        navigator.push(FuroShantenResultScreen(args))
    }
}