package io.ssttkkl.mahjongutils.app.utils.image

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import io.ssttkkl.mahjongutils.app.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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