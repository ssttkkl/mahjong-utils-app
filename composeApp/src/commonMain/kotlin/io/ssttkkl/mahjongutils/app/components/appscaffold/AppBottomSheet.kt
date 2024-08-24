package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class AppBottomSheetState(
    val content: @Composable () -> Unit
) {
    var visible: Boolean by mutableStateOf(false)
    val sheetState: SheetState = SheetState(false)

    companion object {
        val NONE = AppBottomSheetState {}
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    state: AppBottomSheetState,
    resetStateRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(false) }

    // visible表示的是ModalBottomSheet是否真正可见
    // 如果state.visible被设成了false，则调用state.sheetState.hide()开始滑出的动画，动画结束再让ModalBottomSheet滚蛋
    LaunchedEffect(state.visible) {
        if (!state.visible) {
            if (visible) {
                coroutineScope.launch { state.sheetState.hide() }.invokeOnCompletion {
                    if (!state.sheetState.isVisible) {
                        visible = false
                        resetStateRequest()
                    }
                }
            }
        } else {
            visible = true
        }
    }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = {
                state.visible = false
                visible = false
                resetStateRequest()
            },
            sheetState = state.sheetState
        ) {
            state.content()
        }
    }
}
