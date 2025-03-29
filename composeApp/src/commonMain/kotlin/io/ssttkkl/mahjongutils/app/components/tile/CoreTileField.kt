package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import mahjongutils.models.Tile

@Stable
class CoreTileFieldState(
    val interactionSource: MutableInteractionSource
) {
    var selection by mutableStateOf(TextRange.Zero)
}

inline fun CoreTileFieldState.updateSelection(coerceIn: IntRange, action: (TextRange) -> TextRange) {
    selection = action(selection.coerceIn(coerceIn.first, coerceIn.last + 1))
}

@Composable
internal expect fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
    placeholder: String? = null  // 安卓忽略该属性，因为DecoratorBox在ios的输入框上不生效
)