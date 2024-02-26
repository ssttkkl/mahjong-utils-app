@file:OptIn(ExperimentalForeignApi::class)

package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

fun ImageBitmap.toUIImage(): UIImage {
    val bitmap = this.asSkiaBitmap()
    val img = Image.makeFromBitmap(bitmap)
    val data = img.encodeToData() ?: error("encodeToData failed")

    val d = data.bytes.usePinned { bytes ->
        NSData.create(bytes = bytes.addressOf(0), length = bytes.get().size.toULong())
    }

    return UIImage.imageWithData(d) ?: error("UIImage.imageWithData failed")
}
