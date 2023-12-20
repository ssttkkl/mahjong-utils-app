package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.ui.graphics.ImageBitmap

expect object ImageUtils {
    suspend fun saveImage(bitmap: ImageBitmap)
}