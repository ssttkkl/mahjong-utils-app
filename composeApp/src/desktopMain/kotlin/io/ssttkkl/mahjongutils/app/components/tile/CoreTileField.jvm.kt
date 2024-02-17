package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import mahjongutils.models.Tile

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
    placeholder: String?,
) {
    FlowRow(modifier = modifier) {
        Tiles(value, fontSize = fontSizeInSp.sp)
    }
}
