package io.ssttkkl.mahjongutils.app.base.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.ui.CropperPreview
import com.attafitamim.krop.ui.DefaultControls
import com.attafitamim.krop.ui.DefaultTopBar

@Composable
fun ImageCropperDialog(cropState: CropState) {
    Column {
        DefaultTopBar(cropState)
        Box(
            modifier = Modifier
                .weight(1f)
                .clipToBounds()
        ) {
            CropperPreview(state = cropState, modifier = Modifier.fillMaxSize())
            DefaultControls(cropState)
        }
    }
}