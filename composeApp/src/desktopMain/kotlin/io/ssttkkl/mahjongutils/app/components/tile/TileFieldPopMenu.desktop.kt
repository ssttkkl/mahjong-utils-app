package io.ssttkkl.mahjongutils.app.components.tile

import LocalMainWindowState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.asAwtTransferable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.base.utils.toBufferedImage
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_paste_search
import mahjongutils.composeapp.generated.resources.icon_screenshot_frame
import mahjongutils.composeapp.generated.resources.label_recognize_from_clipboard
import mahjongutils.composeapp.generated.resources.label_recognize_from_screenshot
import mahjongutils.composeapp.generated.resources.text_clipboard_no_image
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


@Composable
actual fun TileFieldRecognizeImageMenuItems(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    PickImageMenuItem(onDismissRequest, onImagePicked)
    CaptureMenuItem(onDismissRequest, onImagePicked)
    ClipboardImageMenuItem(onDismissRequest, onImagePicked)
}

@Composable
fun CaptureMenuItem(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    val logger = LoggerFactory.getLogger("CaptureMenuItem")
    // 使用手动的CoroutineScope，避免菜单项的composable移除后协程任务被取消
    val coroutineScope = remember { CoroutineScope(Dispatchers.Main) }
    val mainWindowState = LocalMainWindowState.current

    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val curOnImagePicked by rememberUpdatedState(onImagePicked)

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
                    curOnImagePicked(image.toComposeImageBitmap())
                }
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClipboardImageMenuItem(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    // 从剪切板识别
    val logger = LoggerFactory.getLogger("ClipboardImageMenuItem")
    // 使用手动的CoroutineScope，避免菜单项的composable移除后协程任务被取消
    val coroutineScope = remember { CoroutineScope(Dispatchers.Main) }
    val clipboard = LocalClipboard.current
    val appState = LocalAppState.current

    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val curOnImagePicked by rememberUpdatedState(onImagePicked)

    val noImageInClipboardMsg = stringResource(Res.string.text_clipboard_no_image)

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
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    var image: ImageBitmap? = null

                    val transferable = clipboard.getClipEntry()?.asAwtTransferable ?: return@launch
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

                    if (image != null) {
                        curOnImagePicked(image)
                    } else {
                        appState.snackbarHostState.showSnackbar(noImageInClipboardMsg)
                    }
                    curOnDismissRequest()
                }.onFailure { e -> logger.error(e) }
            }
        }
    )
}