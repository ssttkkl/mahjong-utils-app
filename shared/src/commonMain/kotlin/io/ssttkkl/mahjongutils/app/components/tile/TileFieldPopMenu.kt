package io.ssttkkl.mahjongutils.app.components.tile


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import io.ssttkkl.mahjongutils.app.base.utils.readTiles
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_content_copy
import mahjongutils.composeapp.generated.resources.icon_content_paste
import mahjongutils.composeapp.generated.resources.label_clear
import mahjongutils.composeapp.generated.resources.label_copy
import mahjongutils.composeapp.generated.resources.label_paste
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TileFieldPopMenu(
    expanded: Boolean,
    onAction: (ImeAction) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero
) {
    val clipboard = LocalClipboard.current
    val tileRecognizer = LocalTileRecognizer.current

    var clipboardData by remember { mutableStateOf<List<Tile>?>(null) }

    LaunchedEffect(expanded) {
        clipboardData = clipboard.readTiles()
    }

    val curOnAction by rememberUpdatedState(onAction)
    val curOnDismissRequest by rememberUpdatedState(onDismissRequest)

    // 下拉菜单
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = curOnDismissRequest,
        modifier = modifier,
        offset = offset
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
                onAction(ImeAction.Copy)
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
                onAction(ImeAction.Paste)
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
                onAction(ImeAction.Clear)
                curOnDismissRequest()
            }
        )

        HorizontalDivider()

        // 麻将图像识别的选项组
        tileRecognizer.TileFieldRecognizeImageMenuItems(expanded, curOnAction, curOnDismissRequest)
    }
}
