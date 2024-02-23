package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import dev.icerock.moko.resources.ImageResource

@Composable
expect fun ImageResource.toImageBitmap(): ImageBitmap

@Composable
expect fun ImageResource.toLieDownImageBitmap(): ImageBitmap