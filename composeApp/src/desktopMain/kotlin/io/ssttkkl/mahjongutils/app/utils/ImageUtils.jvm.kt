package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.icerock.moko.resources.ImageResource
import java.awt.image.BufferedImage


@Composable
actual fun ImageResource.toImageBitmap(): ImageBitmap {
    return this.image.toComposeImageBitmap()
}

@Composable
actual fun ImageResource.toLieDownImageBitmap(): ImageBitmap {
    return remember(this) {
        val imageToRotate = this.image
        val widthOfImage: Int = imageToRotate.width
        val heightOfImage: Int = imageToRotate.height
        val typeOfImage: Int = imageToRotate.type

        val newImageFromBuffer = BufferedImage(widthOfImage, heightOfImage, typeOfImage)

        val graphics2D = newImageFromBuffer.createGraphics()

        graphics2D.rotate(
            Math.toRadians(90.0),
            (widthOfImage / 2).toDouble(),
            (heightOfImage / 2).toDouble()
        )
        graphics2D.drawImage(imageToRotate, null, 0, 0)

        newImageFromBuffer.toComposeImageBitmap()
    }
}
