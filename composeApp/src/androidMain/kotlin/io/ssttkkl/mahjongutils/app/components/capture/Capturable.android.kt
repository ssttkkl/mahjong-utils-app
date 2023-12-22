package io.ssttkkl.mahjongutils.app.components.capture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal actual fun CaptureView(
    captureState: CaptureState,
    bounds: Rect,
    heightWrapContent: Boolean,
    widthWrapContent: Boolean,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

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

            drawListener(captureState, coroutineScope, composeView, this)

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
    coroutineScope: CoroutineScope,
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
                        val bitmap = getViewGroupBitmap(viewGroup)
                        coroutineScope.launch {
                            state.captureResult.emit(CaptureResult(bitmap))
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

actual class CaptureResult(
    val bitmap: Bitmap
) {
    actual fun getImageBitmap(): ImageBitmap {
        return bitmap.asImageBitmap()
    }
}