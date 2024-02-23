package io.ssttkkl.mahjongutils.app.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import dev.icerock.moko.resources.ImageResource

@Composable
actual fun ImageResource.toImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(this.drawableResId)
}

@Composable
actual fun ImageResource.toLieDownImageBitmap(): ImageBitmap {
    val bitmap = (this.getDrawable(LocalContext.current) as BitmapDrawable).bitmap
    val matrix = Matrix().apply { postRotate(-90f) }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    return rotatedBitmap.asImageBitmap()
}