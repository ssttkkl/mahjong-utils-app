package io.ssttkkl.mahjongutils.app.utils.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb

actual suspend fun platformWithBackground(
    imageBitmap: ImageBitmap,
    background: Color
): ImageBitmap {
    val newBitmap =
        Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newBitmap)

    // 绘制背景
    val paintBackground = Paint().apply {
        color = background.toArgb()
        style = Paint.Style.FILL
    }
    canvas.drawRect(
        0f,
        0f,
        imageBitmap.width.toFloat(),
        imageBitmap.height.toFloat(),
        paintBackground
    )

    // 绘制原始位图
    canvas.drawBitmap(imageBitmap.asAndroidBitmap(), 50f, 50f, null) // 偏移位置50, 50

    return newBitmap.asImageBitmap()
}