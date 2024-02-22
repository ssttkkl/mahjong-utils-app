package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource

@Composable
fun DrawableResource.toImageBitmap(): ImageBitmap {
    return imageResource(this)
}

@Composable
expect fun DrawableResource.toLieDownImageBitmap(): ImageBitmap