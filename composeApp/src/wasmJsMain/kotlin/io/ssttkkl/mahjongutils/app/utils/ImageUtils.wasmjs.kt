@file:OptIn(ExperimentalResourceApi::class)

package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi


@Composable
actual fun DrawableResource.toLieDownImageBitmap(): ImageBitmap {
    return this.toImageBitmap()
//    val bitmap = imageResource(this).asSkiaBitmap()
//    return remember(bitmap) {
//        val newHeight = bitmap.width
//        val newWidth = bitmap.height
//
//        val buffer = bitmap.readPixels()
//
//        for (x in 0 until bitmap.height) {
//            for (y in 0 until bitmap.width) {
//                rotatedPixelMap
//            }
//        }
//
//        val bitmap = Bitmap()
//        bitmap.allocPixels(ImageInfo.makeS32(newWidth, newHeight, ColorAlphaType.UNPREMUL))
//        bitmap.installPixels(pixels)
//        bitmap.asComposeImageBitmap()
//    }
}
