package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun TileFieldPickImageMenuItems(
    onDismissRequest: () -> Unit,
    onImagePicked: suspend (ImageBitmap) -> Unit
) {
    PickFromImageMenuItem(onDismissRequest, onImagePicked)
}