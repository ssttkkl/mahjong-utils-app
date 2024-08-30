package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap

actual object ImageUtils : CommonImageUtils() {
    actual suspend fun save(imageBitmap: ImageBitmap, title: String): Boolean {
    }
}