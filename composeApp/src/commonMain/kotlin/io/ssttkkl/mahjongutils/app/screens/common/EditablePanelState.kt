package io.ssttkkl.mahjongutils.app.screens.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.screens.base.FormState

class EditablePanelState<F : FormState<ARG>, ARG>(
    var originArgs: ARG,
    val form: F
) {
    var editing by mutableStateOf(false)
}