package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.imageio.ImageIO

actual suspend fun PlatformFile.loadAsImage(): ImageBitmap {
    val image = withContext(Dispatchers.IO) {
        ImageIO.read(file)
    }
    checkNotNull(image) {
        "Failed to read image file: ${file}"
    }
    return image.toComposeImageBitmap()
}