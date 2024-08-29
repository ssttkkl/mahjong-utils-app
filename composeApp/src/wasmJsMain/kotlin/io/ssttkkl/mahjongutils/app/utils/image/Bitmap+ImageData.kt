package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.ImageData

private fun getMethodImplForUint8ClampedArray(obj: Uint8ClampedArray, index: Int): UByte {
    js("return obj[index];")
}

public operator fun Uint8ClampedArray.get(index: Int): UByte =
    getMethodImplForUint8ClampedArray(this, index)

private fun setMethodImplForUint8ClampedArray(obj: Uint8ClampedArray, index: Int, value: UByte) {
    js("obj[index] = value;")
}

public operator fun Uint8ClampedArray.set(index: Int, value: UByte) =
    setMethodImplForUint8ClampedArray(this, index, value)

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

fun ImageData.toBitmap(): Bitmap {
    return Image.makeRaster(
        ImageInfo(
            width,
            height,
            ColorType.RGBA_8888,
            ColorAlphaType.UNPREMUL
        ),
        ByteArray(data.length) { data[it].toByte() },
        width * 4
    ).let {
        Bitmap.makeFromImage(it)
    }
}

fun ImageBitmap.toImageData(): ImageData = asSkiaBitmap().toImageData()