package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.PlatformFile

expect suspend fun PlatformFile.loadAsImage(): ImageBitmap