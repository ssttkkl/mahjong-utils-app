package io.ssttkkl.mahjongutils.app.base.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap

actual suspend fun ImageBitmap.withBackground(background: Color): ImageBitmap {
    val newBitmap = createBitmap(width, height)
    val canvas = Canvas(newBitmap)

    // 绘制背景
    val paintBackground = Paint().apply {
        color = background.toArgb()
        style = Paint.Style.FILL
    }
    canvas.drawRect(
        0f,
        0f,
        width.toFloat(),
        height.toFloat(),
        paintBackground
    )

    // 将原始硬件位图转换为软件位图
    val softwareBitmap = this.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
    // 绘制原始位图
    canvas.drawBitmap(softwareBitmap, 0f, 0f, null)

    return newBitmap.asImageBitmap()
}