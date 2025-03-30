package io.ssttkkl.mahjongutils.app.base.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun ImageBitmap.withBackground(background: Color): ImageBitmap