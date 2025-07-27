package io.ssttkkl.mahjongdetector

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

actual object ImagePreprocessor {
    actual fun preprocessImage(input: ImageBitmap): Pair<ImageBitmap, PaddingInfo> {
        // 转换为 Android Bitmap
        val androidBitmap = input.asAndroidBitmap()

        // 步骤1: 灰度化
        val grayBitmap = convertToGrayscale(androidBitmap)

        // 步骤2: 对比度拉伸
        val contrastBitmap = stretchContrast(grayBitmap)

        // 步骤3: 等比拉伸并填充到 640x640
        val (paddedBitmap, paddingInfo) = scaleAndPadToSquare(contrastBitmap, 640, Color.BLACK)

        return paddedBitmap.asImageBitmap() to paddingInfo
    }

    // 灰度化（通过 ColorMatrix）
    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val grayBitmap = createBitmap(bitmap.width, bitmap.height)
        val canvas = Canvas(grayBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f) // 直接使用饱和度设置为0实现灰度化
        }
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
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
        val squaredBitmap = createBitmap(targetSize, targetSize)
        val canvas = Canvas(squaredBitmap)

        // 填充背景
        canvas.drawColor(background)

        // 计算缩放后的尺寸
        val matrix = Matrix()
        matrix.postScale(scale, scale)

        // 计算居中位置
        matrix.postTranslate(
            (targetSize - scaledWidth) / 2f,
            (targetSize - scaledHeight) / 2f
        )

        canvas.drawBitmap(bitmap, matrix, Paint(Paint.ANTI_ALIAS_FLAG))

        return squaredBitmap to PaddingInfo(scale, padX, padY, bitmap.height, bitmap.width)
    }

    // 对比度拉伸（直方图拉伸）
    private fun stretchContrast(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 找到最小/最大亮度值（灰度图）
        var min = 255
        var max = 0
        for (pixel in pixels) {
            val luminance =
                (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()
            if (luminance < min) min = luminance
            if (luminance > max) max = luminance
        }

        // 线性拉伸
        if (max > min) {
            val range = max - min
            for (i in pixels.indices) {
                val r = Color.red(pixels[i])
                val g = Color.green(pixels[i])
                val b = Color.blue(pixels[i])

                // 计算灰度值
                val gray = (r * 0.299 + g * 0.587 + b * 0.114).toInt()
                // 拉伸
                val stretched = ((gray - min) * 255 / range).coerceIn(0, 255)

                pixels[i] = Color.rgb(stretched, stretched, stretched)
            }
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        }

        return bitmap
    }
}