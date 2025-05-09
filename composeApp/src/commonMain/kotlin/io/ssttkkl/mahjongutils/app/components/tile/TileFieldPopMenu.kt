package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.ssttkkl.mahjongdetector.MahjongDetector
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalImageCropper
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import io.ssttkkl.mahjongutils.app.utils.image.loadAsImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_copy
import mahjongutils.composeapp.generated.resources.icon_content_paste
import mahjongutils.composeapp.generated.resources.label_clear
import mahjongutils.composeapp.generated.resources.label_copy
import mahjongutils.composeapp.generated.resources.label_paste
import mahjongutils.composeapp.generated.resources.label_recognize_from_image
import mahjongutils.composeapp.generated.resources.text_recognize_no_detection
import mahjongutils.models.Tile
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropResult
import network.chaintech.cmpimagepickncrop.imagecropper.cropImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.measureTime


@Composable
fun TileFieldPopMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var clipboardData by remember { mutableStateOf<List<Tile>?>(null) }

    val tileImeHostState = LocalTileImeHostState.current

    LaunchedEffect(expanded) {
        clipboardData = tileImeHostState.readClipboardData()
    }

    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)

    // 下拉菜单
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = curOnDismissRequest,
        modifier = modifier,
    ) {
        // 复制
        DropdownMenuItem(
            text = {
                Row(Modifier.padding(vertical = 8.dp)) {
                    Icon(painterResource(Res.drawable.icon_content_copy), "")
                    Text(
                        stringResource(Res.string.label_copy),
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            onClick = {
                tileImeHostState.emitAction(ImeAction.Copy)
                curOnDismissRequest()
            }
        )

        // 粘贴
        DropdownMenuItem(
            text = {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Icon(painterResource(Res.drawable.icon_content_paste), "")
                        Text(
                            stringResource(Res.string.label_paste),
                            Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    clipboardData?.let { tiles ->
                        Spacer(Modifier.height(8.dp))
                        Row {
                            tiles.forEach {
                                TileImage(it, Modifier.height(24.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            },
            onClick = {
                tileImeHostState.emitAction(ImeAction.Paste)
                curOnDismissRequest()
            },
            enabled = !clipboardData.isNullOrEmpty()
        )

        // 清空
        DropdownMenuItem(
            text = {
                Row(Modifier.padding(vertical = 8.dp)) {
                    Icon(Icons.Default.Clear, "")
                    Text(
                        stringResource(Res.string.label_clear),
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            onClick = {
                tileImeHostState.emitAction(ImeAction.Clear)
                curOnDismissRequest()
            }
        )

        HorizontalDivider()

        // 麻将图像识别的选项组
        val cropper = LocalImageCropper.current
        val appState = LocalAppState.current
        val noDetectionMsg = stringResource(Res.string.text_recognize_no_detection)
        TileFieldPickImageMenuItems(onDismissRequest) { bitmap ->
            val logger = LoggerFactory.getLogger("MahjongDetector")
            val cropResult = cropper.cropImage(bmp = bitmap)
            if (cropResult is ImageCropResult.Success) {
                val res: List<Tile>
                val cost = measureTime {
                    val detections = MahjongDetector.predict(cropResult.bitmap)
                    detections.forEach {
                        logger.debug("detection: ${it}")
                    }
                    res = detections
                        .sortedBy { it.x1 }
                        .map { Tile[it.className] }
                }
                logger.info("result: $res, cost: $cost")
                if (res.isNotEmpty()) {
                    tileImeHostState.emitAction(ImeAction.Replace(res))
                } else {
                    appState.snackbarHostState.showSnackbar(noDetectionMsg)
                }
            }
        }
    }
}

@Composable
expect fun TileFieldPickImageMenuItems(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
)

@Composable
fun PickFromImageMenuItem(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    // 从图片选择
    val logger = remember { LoggerFactory.getLogger("PickFromImageMenuItem") }
    val coroutineScope = remember { CoroutineScope(Dispatchers.Main) }

    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val curOnImagePicked by rememberUpdatedState(onImagePicked)

    val picker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { file ->
        logger.info("pick image: ${file}")

        // 需要在这里消失，避免composable被过早移除导致picker的协程任务被取消
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
                Icon(Icons.Default.Star, "")
                Text(
                    stringResource(Res.string.label_recognize_from_image),
                    Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        onClick = {
            picker.launch()
        }
    )
}