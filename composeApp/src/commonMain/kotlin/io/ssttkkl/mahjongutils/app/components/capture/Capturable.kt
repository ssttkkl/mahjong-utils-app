package io.ssttkkl.mahjongutils.app.components.capture

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

expect class CaptureResult {
    fun getImageBitmap(): ImageBitmap
}

class CaptureState {
    private val mutex = Mutex()

    internal val capturing = MutableStateFlow(false)
    internal val captureResult = MutableSharedFlow<CaptureResult>()

    suspend fun capture(): CaptureResult = mutex.withLock {
        capturing.emit(true)
        return captureResult.first()
    }
}

@Composable
fun rememberCaptureState(): CaptureState {
    return remember {
        CaptureState()
    }
}


@Composable
fun Capturable(
    state: CaptureState,
    heightWrapContent: Boolean = false,
    widthWrapContent: Boolean = false,
    content: @Composable () -> Unit
) {
    var bounds by remember {
        mutableStateOf<Rect?>(null)
    }
    val capturing by state.capturing.collectAsState()

    // 依据状态值 选择是否使用AndroidView进行展示获取截图
    if (capturing && bounds != null) {
        CaptureView(
            captureState = state,
            bounds = bounds!!,
            heightWrapContent = heightWrapContent,
            widthWrapContent = widthWrapContent,
            content = content
        )
    } else {
        Surface(modifier = Modifier.onGloballyPositioned {
            bounds = it.boundsInRoot()
        }, content = content)

    }
}

@Composable
internal expect fun CaptureView(
    captureState: CaptureState,
    bounds: Rect,
    heightWrapContent: Boolean,
    widthWrapContent: Boolean,
    content: @Composable () -> Unit
)