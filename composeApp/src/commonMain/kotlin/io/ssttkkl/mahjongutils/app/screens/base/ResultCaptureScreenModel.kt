package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.components.capture.CaptureState
import io.ssttkkl.mahjongutils.app.utils.saveToAlbum
import kotlinx.coroutines.launch

class ResultCaptureScreenModel : ScreenModel {
    var calculating by mutableStateOf(true)

    val captureState = CaptureState()

    fun onCapture() {
        screenModelScope.launch {
            val result = captureState.capture()
            result.saveToAlbum()
        }
    }
}