package io.ssttkkl.mahjongutils.app.components.capture

import androidx.compose.runtime.Composable

@Composable
actual fun Capturable(
    state: CaptureState,
    content: @Composable () -> Unit
) {
    content()
}