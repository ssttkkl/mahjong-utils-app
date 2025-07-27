package io.ssttkkl.mahjongutils.app.utils.image

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import io.ssttkkl.mahjongutils.app.base.utils.AppInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun PlatformFile.loadAsImage(): ImageBitmap = withContext(Dispatchers.IO) {
    when (val wrapper = androidFile) {
        is AndroidFile.UriWrapper -> {
            AppInstance.app.contentResolver.openInputStream(wrapper.uri)?.use { stream ->
                BitmapFactory.decodeStream(stream).asImageBitmap()
            } ?: error("Failed to open image: ${wrapper.uri}")
        }

        is AndroidFile.FileWrapper -> {
            wrapper.file.inputStream().use { stream ->
                BitmapFactory.decodeStream(stream).asImageBitmap()
            }
        }
    }
}