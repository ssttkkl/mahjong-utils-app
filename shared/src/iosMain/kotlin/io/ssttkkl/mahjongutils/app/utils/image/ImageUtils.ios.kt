package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import io.ssttkkl.mahjongutils.app.base.utils.toUIImage
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.uiViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectGetMidX
import platform.CoreGraphics.CGRectGetMidY
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.popoverPresentationController
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class SaveResult {
    actual val isSupportOpen: Boolean
        get() = false
    actual val isSupportShare: Boolean
        get() = false

    actual suspend fun open() {
    }

    actual suspend fun share() {
    }
}

actual object ImageSaver {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun save(
        appState: AppState,
        imageBitmap: ImageBitmap,
        title: String
    ): SaveResult? {
        val presentingVC = appState.uiViewController
        val uiImage = imageBitmap.toUIImage()
        val activityVC = UIActivityViewController(listOf(uiImage), null)

        // 适配 iPad 弹出样式
        if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
            activityVC.popoverPresentationController?.sourceView = presentingVC.view
            activityVC.popoverPresentationController?.sourceRect = CGRectMake(
                (CGRectGetMidX(presentingVC.view.bounds)),
                CGRectGetMidY(presentingVC.view.bounds),
                0.0, 0.0
            )
        }

        return suspendCoroutine { continuation ->
            presentingVC.presentViewController(activityVC, true) {
                continuation.resume(SaveResult())
            }
        }
    }
}