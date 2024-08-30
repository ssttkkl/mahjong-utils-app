package io.ssttkkl.mahjongutils.app.utils.image

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import io.ssttkkl.mahjongutils.app.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    // 将原始硬件位图转换为软件位图
    val softwareBitmap = imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
    // 绘制原始位图
    canvas.drawBitmap(softwareBitmap, 0f, 0f, null)

    return newBitmap.asImageBitmap()
}

actual object ImageUtils : CommonImageUtils() {
    actual suspend fun save(imageBitmap: ImageBitmap, title: String): Boolean {
        return withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, title)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri: Uri = MyApp.current.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return@withContext false

            uri.let {
                try {
                    MyApp.current.contentResolver.openOutputStream(it)?.use {
                        imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
                    } ?: return@let false
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    MyApp.current.contentResolver.update(uri, values, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@let false
                }
            }
            return@withContext true
        }
    }
}