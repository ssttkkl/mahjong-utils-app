package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropper
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import network.chaintech.cmpimagepickncrop.ui.ImageCropperDialogContainer

@Composable
expect fun ImageCropperHost(
    cropper: ImageCropper = rememberImageCropper(),
    content: @Composable () -> Unit
)

@Composable
fun DefaultImageCropperHost(
    cropper: ImageCropper = rememberImageCropper(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalImageCropper provides cropper
    ) {
        content()
    }

    cropper.imageCropState?.let { cropState ->
        ImageCropperDialogContainer(
            cropState,
            enableRotationOption = false,
            enabledFlipOption = false,
            shapes = null,
            aspects = null
        )
    }
}

val LocalImageCropper = compositionLocalOf<ImageCropper> {
    error("CompositionLocal LocalImageCropper not present")
}