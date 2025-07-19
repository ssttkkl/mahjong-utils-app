package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.ssttkkl.mahjongutils.app.utils.image.loadAsImage
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_photo_camera
import mahjongutils.composeapp.generated.resources.label_recognize_from_image
import mahjongutils.models.Tile
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropper
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

actual class TileRecognizer actual constructor(
    cropper: ImageCropper,
    onResult: suspend (List<Tile>?) -> Unit
) : BaseTileRecognizer(cropper, onResult) {
    @Composable
    actual override fun TileFieldRecognizeImageMenuItems(onDismissRequest: () -> Unit) {
        super.TileFieldRecognizeImageMenuItems(onDismissRequest)
        CameraMenuItem(onDismissRequest)
    }

    @Composable
    fun CameraMenuItem(onDismissRequest: () -> Unit) {
        // 拍照识别
        val curOnDismissRequest by rememberUpdatedState(onDismissRequest)
        val curOnResult by rememberUpdatedState(onResult)

        val launcher = rememberCameraPickerLauncher { file ->
            logger.info("take photo: ${file}")

            // 需要选图完毕后再消失，避免composable被过早移除导致picker的协程任务被取消
            curOnDismissRequest()

            coroutineScope.launch {
                runCatching {
                    val bitmap = file?.loadAsImage()
                    if (bitmap != null) {
                        curOnResult(cropAndRecognizeFromBitmap(bitmap))
                    }
                }.onFailure { e -> logger.error(e) }
            }
        }

        DropdownMenuItem(
            text = {
                Row(Modifier.padding(vertical = 8.dp)) {
                    Icon(vectorResource(Res.drawable.icon_photo_camera), "")
                    Text(
                        stringResource(Res.string.label_recognize_from_image),
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            onClick = {
                logger.info("start take photo")
                launcher.launch()
            }
        )
    }

    actual override suspend fun readClipboardBitmap(clipboard: Clipboard): ImageBitmap? {
        return null
    }
}