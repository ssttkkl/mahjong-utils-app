package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class AppDialogState(
    val dialog: @Composable (onDismissRequest: () -> Unit) -> Unit
) {
    var visible: Boolean by mutableStateOf(false)

    companion object {
        val NONE = AppDialogState {}
    }
}

@Composable
fun AppDialog(
    state: AppDialogState,
    resetStateRequest: () -> Unit
) {
    if (state.visible) {
        state.dialog {
            state.visible = false
            resetStateRequest()
        }
    }
}
