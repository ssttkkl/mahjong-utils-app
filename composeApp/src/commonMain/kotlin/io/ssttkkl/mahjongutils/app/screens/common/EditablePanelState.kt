package io.ssttkkl.mahjongutils.app.screens.common

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.screens.base.FormState

@Stable
class EditablePanelState<F : FormState<ARG>, ARG>(
    originArgs: ARG,
    val form: F
) {
    var originArgs by mutableStateOf(originArgs)
    var editing by mutableStateOf(false)
}