package io.ssttkkl.mahjongutils.app.utils.os

import androidx.compose.ui.graphics.ImageBitmap

expect object ImageUtils {
    suspend fun saveImage(bitmap: ImageBitmap)
}