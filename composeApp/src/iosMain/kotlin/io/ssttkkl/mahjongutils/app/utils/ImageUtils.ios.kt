package io.ssttkkl.mahjongutils.app.utils

import io.ssttkkl.mahjongutils.app.components.capture.CaptureResult
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSError
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIImageWriteToSavedPhotosAlbum
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual suspend fun CaptureResult.saveToAlbum() {
    UIImageWriteToSavedPhotosAlbum(
        uiImage,
        null,
        null,
        null
    )
}