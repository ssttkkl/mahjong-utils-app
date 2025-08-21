package io.ssttkkl.mahjongutils.app.base.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.vinceglb.filekit.PlatformFile
import io.ssttkkl.mahjongdetector.get
import org.jetbrains.skia.Image
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual suspend fun PlatformFile.loadAsImage(): ImageBitmap {
    val buffer: ArrayBuffer = suspendCoroutine { cont ->
        val reader = FileReader()
        reader.onload = { e ->
            cont.resume(reader.result as ArrayBuffer)
        }
        reader.onerror = { e ->
            cont.resumeWithException(RuntimeException("Error occurred reading file: ${file.name}"))
        }
        reader.readAsArrayBuffer(file)
    }
    val bytesJs = Uint8ClampedArray(buffer)
    val bytes = ByteArray(buffer.byteLength) { bytesJs[it].toByte() }
    val image = Image.makeFromEncoded(bytes)
    return image.toComposeImageBitmap()
}