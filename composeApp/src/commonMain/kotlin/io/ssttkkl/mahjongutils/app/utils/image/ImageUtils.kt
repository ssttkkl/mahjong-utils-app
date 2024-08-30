package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun platformWithBackground(imageBitmap: ImageBitmap, background: Color): ImageBitmap

open class CommonImageUtils {
    suspend fun withBackground(imageBitmap: ImageBitmap, background: Color): ImageBitmap =
        platformWithBackground(imageBitmap, background)
}

expect class SaveResult {
    val isSupportOpen: Boolean
    val isSupportShare: Boolean
    suspend fun open()
    suspend fun share()
}

expect object ImageUtils : CommonImageUtils {
    suspend fun save(imageBitmap: ImageBitmap, title: String): SaveResult?
}

