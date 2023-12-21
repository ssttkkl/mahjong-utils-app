package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun TextStyle.withAlpha(alpha: Float) = copy(color = color.copy(alpha))

class TileTextSize(
    val bodyLarge: TextUnit,
    val bodyMedium: TextUnit,
    val bodySmall: TextUnit,
) {
    companion object {
        val Default = TileTextSize(24.sp, 20.sp, 16.sp)
    }
}

val LocalTileTextSize = compositionLocalOf {
    TileTextSize.Default.bodyMedium
}