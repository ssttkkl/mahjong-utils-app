package io.ssttkkl.mahjongutils.app.components.capture

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ComposeUIViewController
import io.ssttkkl.mahjongutils.app.utils.toSkiaImage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIGraphicsBeginImageContext
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun CaptureView(
    captureState: CaptureState,
    bounds: Rect,
    heightWrapContent: Boolean,
    widthWrapContent: Boolean,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val width = with(LocalDensity.current) { bounds.width.toDp() }
    val height = with(LocalDensity.current) { bounds.height.toDp() }

    UIKitView(
        modifier = Modifier.size(width, height),
        factory = {
            val window = UIWindow()
            window.rootViewController = ComposeUIViewController { content() }

            coroutineScope.launch(Dispatchers.Main) {
                UIGraphicsBeginImageContext(CGSizeMake(width.value.toDouble(), height.value.toDouble()))
                val context = UIGraphicsGetCurrentContext()
                window.layer.renderInContext(context)
                val image = UIGraphicsGetImageFromCurrentImageContext()
                UIGraphicsEndImageContext()

                if (image == null) {
                    error("image is null")
                }

                captureState.captureResult.emit(CaptureResult(image))
                captureState.capturing.value = false
            }

            window
        }
    )

    content()
}

actual data class CaptureResult(
    val uiImage: UIImage
) {
    @OptIn(ExperimentalForeignApi::class)
    actual fun getImageBitmap(): ImageBitmap {
        return uiImage.CGImage?.toSkiaImage()?.toComposeImageBitmap()
            ?: error("cannot get image")
    }
}
