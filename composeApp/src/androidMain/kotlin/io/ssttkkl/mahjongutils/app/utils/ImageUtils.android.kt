@file:OptIn(ExperimentalResourceApi::class)

package io.ssttkkl.mahjongutils.app.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
actual fun DrawableResource.toLieDownImageBitmap(): ImageBitmap {
    val bitmap = this.toImageBitmap().asAndroidBitmap()
    val matrix = Matrix().apply { postRotate(-90f) }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    return rotatedBitmap.asImageBitmap()
}