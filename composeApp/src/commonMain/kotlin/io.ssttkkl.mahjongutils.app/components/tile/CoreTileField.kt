package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import mahjongutils.models.Tile

class CoreTileFieldState {
    var focused by mutableStateOf(false)
    var selection by mutableStateOf(TextRange.Zero)
}

@Composable
internal expect fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState = remember { CoreTileFieldState() },
    label: String? = null,
    isError: Boolean = false
)