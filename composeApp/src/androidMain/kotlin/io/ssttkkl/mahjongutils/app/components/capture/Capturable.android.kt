package io.ssttkkl.mahjongutils.app.components.capture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch

@Composable
actual fun Capturable(
    state: CaptureState,
    heightWrapContent: Boolean,
    widthWrapContent: Boolean,
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
private fun CaptureView(
    captureState: CaptureState,
    bounds: Rect,
    heightWrapContent: Boolean,
    widthWrapContent: Boolean,
    content: @Composable () -> Unit
) {
    AndroidView(factory = {
        object : FrameLayout(it) {
            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(
                    if (widthWrapContent) MeasureSpec.UNSPECIFIED else widthMeasureSpec,
                    if (heightWrapContent) MeasureSpec.UNSPECIFIED else heightMeasureSpec
                )
            }
        }.apply {
            layoutParams = ViewGroup.LayoutParams(
                if (widthWrapContent) WRAP_CONTENT else (bounds.right - bounds.left).toInt(),
                if (heightWrapContent) WRAP_CONTENT else (bounds.bottom - bounds.top).toInt()
            )

            val composeView = ComposeView(it).apply {
                setContent {
                    content()
                }
            }

            drawListener(captureState, composeView, this)

            addView(
                composeView, ViewGroup.LayoutParams(
                    if (widthWrapContent) WRAP_CONTENT else MATCH_PARENT,
                    if (heightWrapContent) WRAP_CONTENT else MATCH_PARENT
                )
            )

        }
    })
}

private fun drawListener(
    state: CaptureState,
    view: View,
    viewGroup: ViewGroup
) {
    val drawListener = object : ViewTreeObserver.OnDrawListener {
        var remove = false
        override fun onDraw() {
            if (view.width > 0) {
                if (!remove) {
                    // View 绘制第一帧 开始截图并移除 监听，随后切换截图状态 回到Compose组件
                    remove = true
                    view.post {
                        val bitmap = getViewGroupBitmap(viewGroup).asImageBitmap()
                        state.coroutineScope.launch {
                            state.captureResult.emit(bitmap)
                        }
                        state.capturing.value = false
                        view.viewTreeObserver.removeOnDrawListener(this)
                    }
                }

            }
        }
    }
    view.viewTreeObserver.addOnDrawListener(drawListener)
}

/**
 * @param viewGroup viewGroup
 * @return Bitmap
 */
private fun getViewGroupBitmap(viewGroup: ViewGroup): Bitmap {
    val bitmap = Bitmap.createBitmap(viewGroup.width, viewGroup.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    viewGroup.draw(canvas)
    return bitmap
}
