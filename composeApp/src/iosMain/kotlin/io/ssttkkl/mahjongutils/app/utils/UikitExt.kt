@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LocalComposeEnvironment
import org.jetbrains.compose.resources.LocalResourceReader
import org.jetbrains.compose.resources.getResourceItemByEnvironment
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.UIKit.UIColor
import platform.UIKit.UIImage


@OptIn(InternalResourceApi::class)
@Composable
fun DrawableResource.toUIImage(): UIImage {
    val resource = this
    val environment = LocalComposeEnvironment.current.rememberEnvironment()
    val filePath =
        remember(resource, environment) { resource.getResourceItemByEnvironment(environment).path }
    if (filePath.endsWith(".xml", true) || filePath.endsWith(".svg", true)) {
        error("cannot convert vector")
    }

    val resourceReader = LocalResourceReader.current
    return remember(filePath, resourceReader) {
        val data = NSFileManager.defaultManager()
            .contentsAtPath(NSBundle.mainBundle.resourcePath + "/compose-resources/" + filePath)
        data?.let { UIImage.imageWithData(it) } ?: error("failed to load image")
    }
}

internal fun Color.toUIColor(): UIColor? =
    if (this == Color.Unspecified) {
        null
    } else {
        UIColor(
            red = red.toDouble(),
            green = green.toDouble(),
            blue = blue.toDouble(),
            alpha = alpha.toDouble(),
        )
    }
