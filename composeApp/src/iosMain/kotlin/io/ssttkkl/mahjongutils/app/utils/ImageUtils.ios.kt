package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGAffineTransformMakeRotation
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextSetAllowsAntialiasing
import platform.CoreGraphics.CGContextSetInterpolationQuality
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetBitsPerComponent
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectApplyAffineTransform
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.kCGInterpolationNone
import platform.UIKit.UIImage
import platform.posix.M_PI


@Composable
fun DrawableResource.toUIImage(): UIImage {
    TODO()
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun DrawableResource.toLieDownImageBitmap(): ImageBitmap {
    val uiImage = this.toUIImage()
    return remember(this) {
        // https://gist.github.com/paolonl/6231410
        val imgRef = uiImage.CGImage()
        val angleInRadians = (90) * (M_PI / 180)
        val width = CGImageGetWidth(imgRef).toDouble()
        val height = CGImageGetHeight(imgRef).toDouble()

        val imgRect = CGRectMake(
            0.0,
            0.0,
            width,
            height
        )
        val transform = CGAffineTransformMakeRotation(angleInRadians)
        val rotatedRect = CGRectApplyAffineTransform(imgRect, transform)

        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val bmContext = CGBitmapContextCreate(
            null,
            rotatedRect.useContents { size.width }.toULong(),
            rotatedRect.useContents { size.height }.toULong(),
            CGImageGetBitsPerComponent(imgRef),
            0u,
            colorSpace,
            CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst.value
        )

        CGContextSetAllowsAntialiasing(bmContext, false)
        CGContextSetInterpolationQuality(bmContext, kCGInterpolationNone);
        CGColorSpaceRelease(colorSpace);
        CGContextTranslateCTM(
            bmContext,
            rotatedRect.useContents { size.width / 2 }.toDouble(),
            rotatedRect.useContents { size.height / 2 }.toDouble()
        )
        CGContextRotateCTM(bmContext, angleInRadians);
        CGContextDrawImage(
            bmContext, CGRectMake(-width / 2, -height / 2, width, height),
            imgRef
        )

        val rotatedImage = CGBitmapContextCreateImage(bmContext);
        CFRelease(bmContext);

        rotatedImage?.toSkiaImage()?.toComposeImageBitmap()
            ?: error("fail to convert $this to ImageBitmap")
    }
}
