package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun platformWithBackground(imageBitmap: ImageBitmap, background: Color): ImageBitmap

open class CommonImageUtils {
    suspend fun withBackground(imageBitmap: ImageBitmap, background: Color): ImageBitmap =
        platformWithBackground(imageBitmap, background)
}

expect object ImageUtils : CommonImageUtils {
    suspend fun save(imageBitmap: ImageBitmap, title: String): Boolean
}

