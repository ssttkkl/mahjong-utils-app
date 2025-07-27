package io.ssttkkl.mahjongutils.app.base.utils

import java.awt.Image
import java.awt.image.BufferedImage

fun Image.toBufferedImage(): BufferedImage {
    if (this is BufferedImage) {
        return this
    }

    // 创建透明/不透明的BufferedImage（根据原图类型）
    val bimage = BufferedImage(
        getWidth(null),
        getHeight(null),
        BufferedImage.TYPE_INT_ARGB // 带透明度
        // 或用 TYPE_INT_RGB（无透明度）
    )

    // 将原图绘制到BufferedImage
    val bGr = bimage.createGraphics()
    bGr.drawImage(this, 0, 0, null)
    bGr.dispose() // 释放资源

    return bimage
}