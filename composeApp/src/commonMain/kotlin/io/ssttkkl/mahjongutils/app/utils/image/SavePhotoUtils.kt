package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap

expect object SavePhotoUtils {
    suspend fun save(imageBitmap: ImageBitmap, title: String): Boolean
}