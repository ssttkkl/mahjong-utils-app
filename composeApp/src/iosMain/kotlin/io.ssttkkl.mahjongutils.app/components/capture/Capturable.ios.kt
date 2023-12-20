package io.ssttkkl.mahjongutils.app.components.capture

import androidx.compose.runtime.Composable

@Composable
actual fun Capturable(
    state: CaptureState,
    heightWrapContent: Boolean,
    widthWrapContent: Boolean,
    content: @Composable () -> Unit
) {
    content()
}