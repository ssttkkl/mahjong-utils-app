package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.ui.text.TextStyle

fun TextStyle.withAlpha(alpha: Float) = copy(color = color.copy(alpha))