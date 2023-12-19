package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mahjongutils.models.Tile

@Composable
actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float
) {
}