package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect

actual object ImagePreprocessor {
    actual fun preprocessImage(input: ImageBitmap): Pair<ImageBitmap, PaddingInfo> {
        // 转换为 Skia Bitmap
        val skiaBitmap = input.asSkiaBitmap()

        // 步骤1: 灰度化
        val grayBitmap = convertToGrayscale(skiaBitmap)

        // 步骤2: 对比度拉伸
        val contrastBitmap = stretchContrast(grayBitmap)

        // 步骤3: 等比拉伸并填充到 640x640
        val (paddedBitmap, paddingInfo) = scaleAndPadToSquare(contrastBitmap, 640, Color.BLACK)

        return paddedBitmap.asComposeImageBitmap() to paddingInfo
    }

    // 灰度化（通过 ColorMatrix）
    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val grayBitmap = Bitmap().apply {
            allocPixels(bitmap.imageInfo.withColorType(ColorType.GRAY_8))
        }
        val paint = Paint().apply {
            colorFilter = ColorFilter.makeMatrix(
                ColorMatrix(
                    0.299f, 0.587f, 0.114f, 0f, 0f, // R
                    0.299f, 0.587f, 0.114f, 0f, 0f, // G
                    0.299f, 0.587f, 0.114f, 0f, 0f, // B
                    0f, 0f, 0f, 1f, 0f             // A
                )
            )
        }
        Canvas(grayBitmap).apply {
            drawImage(Image.makeFromBitmap(bitmap), 0f, 0f, paint)
            close()
        }
        return grayBitmap
    }

    // 等比缩放并填充到目标尺寸
    private fun scaleAndPadToSquare(
        bitmap: Bitmap,
        targetSize: Int,
        background: Int
    ): Pair<Bitmap, PaddingInfo> {
        val scale = targetSize.toFloat() / maxOf(bitmap.width, bitmap.height)
        val scaledWidth = (bitmap.width * scale).toInt()
        val scaledHeight = (bitmap.height * scale).toInt()
        val padX = (targetSize - scaledWidth) / 2
        val padY = (targetSize - scaledHeight) / 2

        // 创建目标 Bitmap 并填充
        val squaredBitmap = Bitmap().apply {
            allocPixels(ImageInfo.makeN32Premul(targetSize, targetSize))
        }
        Canvas(squaredBitmap).apply {
            clear(background) // 填充白色背景（可自定义）
            drawImageRect(
                Image.makeFromBitmap(bitmap),
                Rect.makeXYWH(
                    (targetSize - scaledWidth) / 2f,
                    (targetSize - scaledHeight) / 2f,
                    scaledWidth.toFloat(),
                    scaledHeight.toFloat()
                )
            )
            close()
        }
        return squaredBitmap to PaddingInfo(scale, padX, padY, bitmap.height, bitmap.width)
    }

    // 对比度拉伸（直方图拉伸）
    private fun stretchContrast(bitmap: Bitmap): Bitmap {
        val pixels = checkNotNull(bitmap.readPixels())

        // 找到最小/最大亮度值（灰度图）
        var min = 255
        var max = 0
        for (i in pixels.indices) {
            val luminance = pixels[i].toInt() and 0xFF
            if (luminance < min) min = luminance
            if (luminance > max) max = luminance
        }

        // 线性拉伸
        if (max > min) {
            val range = max - min
            for (i in pixels.indices) {
                val stretched = ((pixels[i].toInt() and 0xFF - min) * 255 / range).coerceIn(0, 255)
                pixels[i] = stretched.toByte()
            }
            bitmap.installPixels(bitmap.imageInfo, pixels, bitmap.rowBytes)
        }

        return bitmap
    }
}