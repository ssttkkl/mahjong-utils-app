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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.base.utils.toImageBitmap
import io.ssttkkl.mahjongutils.app.base.utils.toImageData
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_paste_search
import mahjongutils.composeapp.generated.resources.label_recognize_from_clipboard
import mahjongutils.composeapp.generated.resources.text_clipboard_no_image
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.w3c.files.Blob

@Composable
actual fun TileFieldRecognizeImageMenuItems(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    PickImageMenuItem(onDismissRequest, onImagePicked)
    ClipboardImageMenuItem(onDismissRequest, onImagePicked)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClipboardImageMenuItem(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    // 从剪切板识别
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
            coroutineScope.launch {
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

                if (blob == null) {
                    appState.snackbarHostState.showSnackbar(noImageInClipboardMsg)
                    return@launch
                }

                val bitmap = blob.toImageData().toImageBitmap()
                curOnImagePicked(bitmap)
                curOnDismissRequest()
            }
        }
    )
}