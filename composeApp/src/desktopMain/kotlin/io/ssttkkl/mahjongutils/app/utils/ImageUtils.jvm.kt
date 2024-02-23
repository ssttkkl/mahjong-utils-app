@file:OptIn(ExperimentalResourceApi::class)

package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage


@Composable
actual fun DrawableResource.toLieDownImageBitmap(): ImageBitmap {
    val bitmap = imageResource(this)
    return remember(bitmap) {
        bitmap.toAwtImage().rotateImageByDegrees(-90.0).toComposeImageBitmap()
    }
}

private fun BufferedImage.rotateImageByDegrees(angle: Double): BufferedImage {
    val rads = Math.toRadians(angle)
    val sin = Math.abs(Math.sin(rads))
    val cos = Math.abs(Math.cos(rads))
    val w = this.width
    val h = this.height
    val newWidth = Math.floor(w * cos + h * sin).toInt()
    val newHeight = Math.floor(h * cos + w * sin).toInt()
    val rotated = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = rotated.createGraphics()
    val at = AffineTransform()
    at.translate(((newWidth - w) / 2).toDouble(), ((newHeight - h) / 2).toDouble())
    val x = w / 2
    val y = h / 2
    at.rotate(rads, x.toDouble(), y.toDouble())
    g2d.transform = at
    g2d.drawImage(this, null, 0, 0)
    g2d.dispose()
    return rotated
}