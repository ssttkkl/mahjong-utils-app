package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap

expect object MahjongDetector {
    suspend fun predict(image: ImageBitmap): List<String>
}

internal val MahjongDetector.CLASS_NAME
    get() = listOf(
        "1m", "1p", "1s",
        "2m", "2p", "2s",
        "3m", "3p", "3s",
        "4m", "4p", "4s",
        "5m", "5p", "5s",
        "6m", "6p", "6s",
        "7m", "7p", "7s",
        "8m", "8p", "8s",
        "9m", "9p", "9s",
        "chun", "haku", "hatsu", "nan", "pe", "sha", "tou"
    )