package io.ssttkkl.mahjongdetector

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.ImageData

fun Bitmap.toImageData(): ImageData {
    val pixels = readPixels(
        ImageInfo(
            width,
            height,
            ColorType.RGBA_8888,
            ColorAlphaType.UNPREMUL
        )
    ) ?: error("fail to read pixels")

    // 转换为ImageData
    val imageDataArray = Uint8ClampedArray(pixels.size).apply {
        pixels.indices.forEach { i ->
            this[i] = pixels[i].toUByte()
        }
    }
    return ImageData(imageDataArray, width, height)
}