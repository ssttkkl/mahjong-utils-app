package io.ssttkkl.mahjongutils.app.components.tile

import LocalMainWindowState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_recognize_from_screenshot
import org.jetbrains.compose.resources.stringResource
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage


@Composable
actual fun TileFieldPickImageMenuItems(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    PickFromImageMenuItem(onDismissRequest, onImagePicked)
    CaptureMenuItem(onDismissRequest, onImagePicked)
}

@Composable
fun CaptureMenuItem(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    val coroutineScope = remember { CoroutineScope(Dispatchers.Main) }
    val mainWindowState = LocalMainWindowState.current
    val logger = LoggerFactory.getLogger("CaptureMenuItem")

    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val curOnImagePicked by rememberUpdatedState(onImagePicked)

    DropdownMenuItem(
        text = {
            Row(Modifier.padding(vertical = 8.dp)) {
                Icon(Icons.Default.Star, "")
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