package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.Clipboard
import com.attafitamim.krop.core.crop.ImageCropper
import io.ssttkkl.mahjongutils.app.base.utils.toImageBitmap
import io.ssttkkl.mahjongutils.app.base.utils.toImageData
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import kotlinx.coroutines.await
import org.w3c.files.Blob

actual class TileRecognizer actual constructor(
    cropper: ImageCropper,
    snackbarHostState: SnackbarHostState,
    noDetectionMsg: String
) : BaseTileRecognizer(cropper, snackbarHostState, noDetectionMsg) {

    @Composable
    actual override fun TileFieldRecognizeImageMenuItems(
        expanded: Boolean,
        onAction: (TileImeHostState.ImeAction) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        super.TileFieldRecognizeImageMenuItems(expanded, onAction, onDismissRequest)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    actual override suspend fun clipboardHasImage(clipboard: Clipboard): Boolean {
        val items = clipboard.getClipEntry()?.clipboardItems
        if (items != null) {
            for (i in 0 until items.length) {
                val item = items[i] ?: continue
                println("item ${i}: ${item}")
                for (j in 0 until item.types.length) {
                    val type = item.types[j] ?: continue
                    println("item ${i} type ${j}: ${type}")
                    if (type.toString().startsWith("image/")) {
                        return true
                    }
                }
            }
        }

        return false
    }

    @OptIn(ExperimentalComposeUiApi::class)
    actual override suspend fun readClipboardImage(clipboard: Clipboard): ImageBitmap? {
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

