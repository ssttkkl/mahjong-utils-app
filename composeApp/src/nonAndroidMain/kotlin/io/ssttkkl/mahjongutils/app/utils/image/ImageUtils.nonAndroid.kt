package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface

actual suspend fun platformWithBackground(
    imageBitmap: ImageBitmap,
    background: Color
): ImageBitmap {
    // 创建一个新的图像位图用于存放背景和原始图像
    val surface = Surface.makeRasterN32Premul(imageBitmap.width, imageBitmap.height)
    val canvas = surface.canvas

    // 绘制背景
    val paintBackground = Paint().apply {
        color = background.toArgb()
    }
    canvas.drawRect(
        Rect.makeLTRB(
            0f,
            0f,
            imageBitmap.width.toFloat(),
            imageBitmap.height.toFloat()
        ), paintBackground
    )

    // 绘制原始图像
    val skiaImage = Image.makeFromBitmap(imageBitmap.asSkiaBitmap())
    canvas.drawImage(skiaImage, 0.0f, 0.0f)

    // 返回新的 ImageBitmap
    return surface.makeImageSnapshot().toComposeImageBitmap()
}