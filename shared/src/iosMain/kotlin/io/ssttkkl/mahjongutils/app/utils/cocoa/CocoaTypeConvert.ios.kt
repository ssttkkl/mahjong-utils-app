@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package io.ssttkkl.mahjongutils.app.base.utils.cocoa

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LocalComposeEnvironment
import org.jetbrains.compose.resources.LocalResourceReader
import org.jetbrains.compose.resources.getResourceItemByEnvironment
import org.jetbrains.skia.Image
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.create
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

fun ImageBitmap.toUIImage(): UIImage {
    return UIImage(data = this.toNSData())
}

fun ImageBitmap.toNSData(): NSData {
    val bytes = Image.makeFromBitmap(this.asSkiaBitmap()).encodeToData()?.bytes
    return bytes?.toNSData()
        ?: throw IllegalArgumentException("Error converting image for iOS consumption, please investigate")
}

@OptIn(
    ExperimentalForeignApi::class
)
fun ByteArray.toNSData(): NSData = usePinned {
    NSData.create(bytes = it.addressOf(0), this.size.convert())
}