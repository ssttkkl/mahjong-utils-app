package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.utils.image.loadAsImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_photo_camera
import mahjongutils.composeapp.generated.resources.label_recognize_from_image
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
actual fun TileFieldPickImageMenuItems(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    PickFromImageMenuItem(onDismissRequest, onImagePicked)
    CameraMenuItem(onDismissRequest, onImagePicked)
}

@Composable
fun CameraMenuItem(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    // 拍照识别
    val logger = remember { LoggerFactory.getLogger("CameraMenuItem") }
    // 使用手动的CoroutineScope，避免菜单项的composable移除后协程任务被取消
    val coroutineScope = remember { CoroutineScope(Dispatchers.Main) }

    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val curOnImagePicked by rememberUpdatedState(onImagePicked)

    val launcher = rememberCameraPickerLauncher { file ->
        logger.info("pick image: ${file}")

        // 需要选图完毕后再消失，避免composable被过早移除导致picker的协程任务被取消
        curOnDismissRequest()

        coroutineScope.launch {
            runCatching {
                val bitmap = file?.loadAsImage()
                if (bitmap != null) {
                    curOnImagePicked(bitmap)
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
            launcher.launch()
        }
    )
}