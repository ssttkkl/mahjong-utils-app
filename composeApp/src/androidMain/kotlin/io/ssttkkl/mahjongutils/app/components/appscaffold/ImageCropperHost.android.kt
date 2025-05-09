package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropper

@Composable
actual fun ImageCropperHost(
    cropper: ImageCropper,
    content: @Composable () -> Unit
) {
    DefaultImageCropperHost(cropper, content)
}