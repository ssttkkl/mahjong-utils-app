package io.ssttkkl.mahjongutils.app.utils

import io.ssttkkl.mahjongutils.app.components.capture.CaptureResult
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

@OptIn(ExperimentalForeignApi::class)
actual suspend fun CaptureResult.saveToAlbum() {
    UIImageWriteToSavedPhotosAlbum(
        uiImage,
        null,
        null,
        null
    )
}