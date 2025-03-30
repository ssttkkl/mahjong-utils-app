package io.ssttkkl.mahjongutils.app.base.components

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // ignored, ios doesn't have back press
}