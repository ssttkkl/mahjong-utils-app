package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.title_crop_image
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropper
import network.chaintech.cmpimagepickncrop.ui.ImageCropperDialogContainer
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ImageCropperHost(
    cropper: ImageCropper,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalImageCropper provides cropper
    ) {
        content()
    }
    cropper.imageCropState?.let { cropState ->
        Window(
            onCloseRequest = { cropState.done(accept = false) },
            title = stringResource(Res.string.title_crop_image)
        ) {
            ImageCropperDialogContainer(
                cropState,
                enableRotationOption = false,
                enabledFlipOption = false,
                shapes = null,
                aspects = null
            )
        }
    }
}