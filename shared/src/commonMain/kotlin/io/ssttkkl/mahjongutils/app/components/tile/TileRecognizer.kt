package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.rememberImageCropper
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.ssttkkl.mahjongdetector.MahjongDetector
import io.ssttkkl.mahjongutils.app.base.components.ImageCropperDialog
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import io.ssttkkl.mahjongutils.app.utils.image.loadAsImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_paste_search
import mahjongutils.composeapp.generated.resources.icon_image
import mahjongutils.composeapp.generated.resources.label_recognize_from_clipboard
import mahjongutils.composeapp.generated.resources.label_recognize_from_image
import mahjongutils.composeapp.generated.resources.text_clipboard_no_image
import mahjongutils.composeapp.generated.resources.text_recognize_no_detection
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.measureTime

sealed class TileRecognizeResult {
    data object Abort : TileRecognizeResult()
    data class Success(val data: List<Tile>) : TileRecognizeResult()
}

expect class TileRecognizer : BaseTileRecognizer {
    constructor(
        cropper: ImageCropper,
        snackbarHostState: SnackbarHostState,
        noDetectionMsg: String
    )

    @Composable
    override fun TileFieldRecognizeImageMenuItems(
        expanded: Boolean,  // 这里的expand是指外层的菜单是否展开，用于在展开时更新一些状态
        onAction: (ImeAction) -> Unit,
        onDismissRequest: () -> Unit
    )

    override suspend fun clipboardHasImage(clipboard: Clipboard): Boolean

    override suspend fun readClipboardImage(clipboard: Clipboard): ImageBitmap?
}

abstract class BaseTileRecognizer(
    val cropper: ImageCropper,
    val snackbarHostState: SnackbarHostState,
    val noDetectionMsg: String
) {
    // 使用手动的CoroutineScope，避免菜单项的composable移除后协程任务被取消
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    val logger = LoggerFactory.getLogger("TileRecognizer")

    @Composable
    open fun TileFieldRecognizeImageMenuItems(
        expanded: Boolean,
        onAction: (ImeAction) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        PickImageMenuItem(onAction, onDismissRequest)
        ClipboardImageMenuItem(expanded, onAction, onDismissRequest)
    }

    @Composable
    fun PickImageMenuItem(
        onAction: (ImeAction) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        val curOnAction by rememberUpdatedState(onAction)
        val curOnDismissRequest by rememberUpdatedState(onDismissRequest)

        // 从图片识别
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
                        cropAndRecognizeAndFillFromBitmap(bitmap, curOnAction)
                    }
                }.onFailure { e -> logger.error(e) }
            }
        }

        DropdownMenuItem(
            text = {
                Row(Modifier.padding(vertical = 8.dp)) {
                    Icon(vectorResource(Res.drawable.icon_image), "")
                    Text(
                        stringResource(Res.string.label_recognize_from_image),
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            onClick = {
                logger.info("start pick image")
                picker.launch()
            }
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ClipboardImageMenuItem(
        expanded: Boolean,
        onAction: (ImeAction) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        // 从剪切板识别
        val snackbarHostState = LocalAppState.current.snackbarHostState
        val clipboard = LocalClipboard.current
        val noImageInClipboardMsg = stringResource(Res.string.text_clipboard_no_image)

        val curOnAction by rememberUpdatedState(onAction)
        val curOnDismissRequest by rememberUpdatedState(onDismissRequest)

        var enabled by remember { mutableStateOf<Boolean>(true) }

        LaunchedEffect(expanded) {
            enabled = clipboardHasImage(clipboard)
        }

        DropdownMenuItem(
            text = {
                Row(Modifier.padding(vertical = 8.dp)) {
                    Icon(vectorResource(Res.drawable.icon_content_paste_search), "")
                    Text(
                        stringResource(Res.string.label_recognize_from_clipboard),
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            onClick = {
                coroutineScope.launch {
                    val image = readClipboardImage(clipboard)
                    if (image != null) {
                        logger.info("readClipboardImage: success")
                        cropAndRecognizeAndFillFromBitmap(image, curOnAction)
                    } else {
                        logger.info("readClipboardImage: no image")
                        snackbarHostState.showSnackbar(noImageInClipboardMsg)
                    }
                    curOnDismissRequest()
                }
            },
            enabled = enabled
        )
    }

    suspend fun cropAndRecognizeAndFillFromBitmap(
        bitmap: ImageBitmap,
        onAction: (ImeAction) -> Unit
    ): TileRecognizeResult {
        val res = cropAndRecognizeFromBitmap(bitmap)
        if (res is TileRecognizeResult.Success) {
            if (res.data.isNotEmpty()) {
                onAction(ImeAction.Replace(res.data))
            } else {
                snackbarHostState.showSnackbar(noDetectionMsg)
            }
        }
        return res
    }

    suspend fun cropAndRecognizeFromBitmap(bitmap: ImageBitmap): TileRecognizeResult {
        val cropResult = cropper.crop(bitmap)
        if (cropResult is CropResult.Success) {
            return TileRecognizeResult.Success(
                recognizeFromBitmap(cropResult.bitmap)
            )
        } else if (cropResult is CropError) {
            logger.error("crop error: $cropResult")
        }
        return TileRecognizeResult.Abort
    }

    suspend fun recognizeFromBitmap(bitmap: ImageBitmap): List<Tile> {
        val res: List<Tile>
        val cost = measureTime {
            val detections = MahjongDetector.predict(bitmap)
            detections.forEach {
                logger.debug("detection: ${it}")
            }
            res = detections
                .sortedBy { it.x1 }
                .map { Tile[it.className] }
        }
        logger.info("result: $res, cost: $cost")
        return res
    }

    abstract suspend fun clipboardHasImage(clipboard: Clipboard): Boolean

    abstract suspend fun readClipboardImage(clipboard: Clipboard): ImageBitmap?
}

@Composable
fun rememberTileRecognizer(
    cropper: ImageCropper,
    snackbarHostState: SnackbarHostState
): TileRecognizer {
    val noDetectionMsg = stringResource(Res.string.text_recognize_no_detection)

    return remember(cropper, snackbarHostState, noDetectionMsg) {
        TileRecognizer(cropper, snackbarHostState, noDetectionMsg)
    }
}

@Composable
expect fun TileRecognizerHost(
    appState: AppState,
    content: @Composable () -> Unit
)

@Composable
fun DefaultTileRecognizerHost(
    appState: AppState,
    content: @Composable () -> Unit
) {
    val cropper = rememberImageCropper()
    val tileRecognizer =
        rememberTileRecognizer(cropper, appState.snackbarHostState)

    CompositionLocalProvider(
        LocalTileRecognizer provides tileRecognizer
    ) {
        content()
    }

    cropper.cropState?.let { cropState ->
        ImageCropperDialog(cropState)
    }
}

val LocalTileRecognizer = compositionLocalOf<TileRecognizer> {
    error("No LocalTileRecognizer provided! ")
}
