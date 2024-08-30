package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import kotlinx.browser.document
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

private fun blobOptions(type: String): BlobPropertyBag = js(
    "({ type })"
)

actual object ImageUtils : CommonImageUtils() {
    private fun download(
        buffer: JsAny?,
        filename: String,
        type: String = "application/octet-stream"
    ) {
        // 创建Blob URL
        val blob = Blob(
            JsArray<JsAny?>().apply { set(0, buffer) },
            blobOptions(type)
        )
        val url = URL.createObjectURL(blob)
        download(url, filename)
    }

    private fun download(
        url: String,
        filename: String
    ) {
        try {
            // 创建并触发一个隐藏的 <a> 标签
            val a = document.createElement("a") as HTMLAnchorElement
            a.href = url
            a.download = filename
            document.body?.apply {
                appendChild(a)
                a.click()
                removeChild(a)
            }
        } finally {
            URL.revokeObjectURL(url)
        }
    }

    actual suspend fun save(imageBitmap: ImageBitmap, title: String): Boolean {
        return try {
            val imgData = imageBitmap.toImageData()

            // 创建一个 <canvas> 标签，绘图后保存为png
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            canvas.width = imageBitmap.width
            canvas.height = imageBitmap.height
            val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
            ctx.putImageData(imgData, 0.0, 0.0)
            val url = canvas.toDataURL("image/png")
            download(url, "${title}.png")
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

}