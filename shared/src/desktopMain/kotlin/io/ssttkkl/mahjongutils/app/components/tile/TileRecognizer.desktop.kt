package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.asAwtTransferable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.rememberImageCropper
import io.ssttkkl.mahjongutils.app.base.components.ImageCropperDialog
import io.ssttkkl.mahjongutils.app.base.utils.toBufferedImage
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalMainWindowState
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_screenshot_frame
import mahjongutils.composeapp.generated.resources.label_recognize_from_screenshot
import mahjongutils.composeapp.generated.resources.title_crop_image
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import java.awt.Image
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

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
        CaptureMenuItem(onAction, onDismissRequest)
    }

    @Composable
    fun CaptureMenuItem(
        onAction: (TileImeHostState.ImeAction) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        val mainWindowState = LocalMainWindowState.current

        val curOnAction by rememberUpdatedState(onAction)
        val curOnDismissRequest by rememberUpdatedState(onDismissRequest)

        DropdownMenuItem(
            text = {
                Row(Modifier.padding(vertical = 8.dp)) {
                    Icon(vectorResource(Res.drawable.icon_screenshot_frame), "")
                    Text(
                        stringResource(Res.string.label_recognize_from_screenshot),
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            onClick = {
                curOnDismissRequest()

                coroutineScope.launch {
                    var image: BufferedImage? = null

                    // 把窗口移出屏幕外再截屏，然后恢复
                    val curLocation = mainWindowState.position
                    try {
                        logger.info("start capture")
                        mainWindowState.position = WindowPosition(100000.dp, 100000.dp)
                        image = withContext(Dispatchers.IO) {
                            Robot().createScreenCapture(
                                Rectangle(
                                    Toolkit.getDefaultToolkit().screenSize
                                )
                            )
                        }
                        logger.info("capture image: ${image.height}*${image.width}")
                    } catch (e: Exception) {
                        logger.error(e)
                    } finally {
                        logger.info("finish capture")
                        mainWindowState.position = curLocation
                    }

                    image?.let { image ->
                        cropAndRecognizeAndFillFromBitmap(image.toComposeImageBitmap(), curOnAction)
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    actual override suspend fun clipboardHasImage(clipboard: Clipboard): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val transferable =
                    clipboard.getClipEntry()?.asAwtTransferable ?: return@withContext false

                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    val fileList =
                        transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                    val imgFile = fileList.firstOrNull {
                        Files.probeContentType(it.toPath()).startsWith("image/")
                    }

                    return@withContext imgFile != null
                } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    return@withContext true
                } else {
                    return@withContext false
                }
            } catch (t: Throwable) {
                logger.error("failed to readClipboardImage", t)
                return@withContext false
            }
        }

    @OptIn(ExperimentalComposeUiApi::class)
    actual override suspend fun readClipboardImage(clipboard: Clipboard): ImageBitmap? =
        withContext(Dispatchers.IO) {
            try {
                var image: ImageBitmap? = null

                val transferable =
                    clipboard.getClipEntry()?.asAwtTransferable ?: return@withContext null

                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    val fileList =
                        transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                    val imgFile = fileList.firstOrNull {
                        Files.probeContentType(it.toPath()).startsWith("image/")
                    }

                    if (imgFile != null) {
                        image = ImageIO.read(imgFile)
                            .toBufferedImage().toComposeImageBitmap()
                    }
                } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    image = (transferable.getTransferData(DataFlavor.imageFlavor) as Image)
                        .toBufferedImage().toComposeImageBitmap()
                }

                return@withContext image
            } catch (t: Throwable) {
                logger.error("failed to readClipboardImage", t)
                return@withContext null
            }
        }
}

@Composable
actual fun TileRecognizerHost(
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
        Window(
            onCloseRequest = { cropState.done(accept = false) },
            title = stringResource(Res.string.title_crop_image)
        ) {
            ImageCropperDialog(cropState)
        }
    }
}