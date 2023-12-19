package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mahjongutils.models.Tile

@Composable
actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    label: String?,
    isError: Boolean
) {
}