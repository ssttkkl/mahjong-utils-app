package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap

actual class SaveResult {
    actual val isSupportOpen: Boolean
        get() = TODO("Not yet implemented")
    actual val isSupportShare: Boolean
        get() = TODO("Not yet implemented")

    actual suspend fun open() {
    }

    actual suspend fun share() {
    }
}

actual object ImageUtils : CommonImageUtils() {
    actual suspend fun save(imageBitmap: ImageBitmap, title: String): SaveResult? {
        return null
    }
}