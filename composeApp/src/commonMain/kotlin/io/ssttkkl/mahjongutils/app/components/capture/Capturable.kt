package io.ssttkkl.mahjongutils.app.components.capture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first


class CaptureState(
    internal val coroutineScope: CoroutineScope
) {
    internal val capturing = MutableStateFlow(false)
    internal val captureResult = MutableSharedFlow<ImageBitmap>()

    suspend fun capture(): ImageBitmap {
        capturing.emit(true)
        return captureResult.first()
    }
}

@Composable
fun rememberCaptureState(): CaptureState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        CaptureState(coroutineScope)
    }
}

@Composable
expect fun Capturable(
    state: CaptureState = rememberCaptureState(),
    content: @Composable () -> Unit
)