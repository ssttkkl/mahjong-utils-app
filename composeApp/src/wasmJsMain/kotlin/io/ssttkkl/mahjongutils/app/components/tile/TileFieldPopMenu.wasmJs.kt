package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.Clipboard
import io.ssttkkl.mahjongutils.app.base.utils.toImageBitmap
import io.ssttkkl.mahjongutils.app.base.utils.toImageData
import kotlinx.coroutines.await
import mahjongutils.models.Tile
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropper
import org.w3c.files.Blob

actual class TileRecognizer actual constructor(
    cropper: ImageCropper,
    onResult: suspend (List<Tile>?) -> Unit
) : BaseTileRecognizer(cropper, onResult) {

    @Composable
    actual override fun TileFieldRecognizeImageMenuItems(onDismissRequest: () -> Unit) {
        super.TileFieldRecognizeImageMenuItems(onDismissRequest)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    actual override suspend fun readClipboardBitmap(clipboard: Clipboard): ImageBitmap? {
        var blob: Blob? = null

        val items = clipboard.getClipEntry()?.clipboardItems
        if (items != null) {
            for (i in 0 until items.length) {
                val item = items[i] ?: continue
                println("item ${i}: ${item}")
                for (j in 0 until item.types.length) {
                    val type = item.types[j] ?: continue
                    println("item ${i} type ${j}: ${type}")
                    if (type.toString().startsWith("image/")) {
                        blob = item.getType(type).await()
                    }
                }
            }
        }

        return blob?.toImageData()?.toImageBitmap()
    }
}

