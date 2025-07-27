package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import okio.Buffer

fun ImageBitmap.toNchwFp16Buffer(): Buffer {
    val pixels = IntArray(height * width)
    readPixels(pixels)

    val buffer = Buffer()

    for (c in 0 until 3) {
        for (pixel in pixels) {
            // c=0 -> R, c=1 -> G, c=2 -> B
            val v = (pixel shr (c * 8)) and 0xFF
            buffer.writeShort(floatToFp16(v / 255.0f).toInt())
        }
    }

    return buffer
}
