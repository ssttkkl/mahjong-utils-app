package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap

expect object ImagePreprocessor {
    fun preprocessImage(input: ImageBitmap): Pair<ImageBitmap, PaddingInfo>
}