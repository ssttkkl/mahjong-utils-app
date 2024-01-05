package io.ssttkkl.mahjongutils.app.components.backhandler

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)