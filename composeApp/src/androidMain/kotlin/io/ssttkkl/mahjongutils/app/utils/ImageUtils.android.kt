package io.ssttkkl.mahjongutils.app.utils

import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.MediaStore
import io.ssttkkl.mahjongutils.app.MyApp
import io.ssttkkl.mahjongutils.app.components.capture.CaptureResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

actual suspend fun CaptureResult.saveToAlbum() {
    withContext(Dispatchers.IO) {
        with(MyApp.current) {
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        UUID.randomUUID().toString() + ".jpg"
                    )
                    put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                }
            ) ?: return@with

            contentResolver.openOutputStream(uri)?.use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output)
            }
        }
    }
}