package io.ssttkkl.mahjongutils.app.utils

import android.R.attr.height
import android.R.attr.width
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import dev.icerock.moko.resources.ImageResource
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

@Composable
actual fun ImageResource.toImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(this.drawableResId)
}

@Composable
actual fun ImageResource.toLieDownImageBitmap(): ImageBitmap {
    val bitmap = (this.getDrawable(LocalContext.current) as BitmapDrawable).bitmap
    val matrix = Matrix().apply { postRotate(-90f) }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    return rotatedBitmap.asImageBitmap()
}