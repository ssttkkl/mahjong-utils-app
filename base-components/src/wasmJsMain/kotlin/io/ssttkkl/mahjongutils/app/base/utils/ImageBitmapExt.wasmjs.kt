package io.ssttkkl.mahjongutils.app.base.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageData
import org.w3c.files.Blob

// JS: Uint8ClampedArray declaration unusable
// https://youtrack.jetbrains.com/issue/KT-24583/JS-Uint8ClampedArray-declaration-unusable
// 妈的排查了两天才发现这里有个大坑
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

suspend fun Blob.toImageData(): ImageData {
    val bitmap = window.createImageBitmap(this).await<org.w3c.dom.ImageBitmap>()
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    ctx.drawImage(bitmap, 0.0, 0.0)
    return ctx.getImageData(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
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
fun ImageData.toImageBitmap(): ImageBitmap = toBitmap().asComposeImageBitmap()