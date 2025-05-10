package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.vinceglb.filekit.PlatformFile
import org.jetbrains.skia.Image
import org.jetbrains.skia.makeFromEncoded
import platform.Foundation.NSData
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithURL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual suspend fun PlatformFile.loadAsImage(): ImageBitmap {
    val data: NSData = suspendCoroutine { cont ->
        NSURLSession.sharedSession.dataTaskWithURL(nsUrl) { data, response, err ->
            if (err != null) {
                error(err.localizedDescription())
            }
            checkNotNull(data)
            cont.resume(data)
        }
    }

    return Image.makeFromEncoded(data).toComposeImageBitmap()
}