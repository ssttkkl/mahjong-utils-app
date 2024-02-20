package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.sp
import io.ssttkkl.mahjongutils.app.components.tapPress
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import kotlinx.coroutines.launch
import mahjongutils.models.Tile


@OptIn(ExperimentalLayoutApi::class)
@Composable
internal actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
    placeholder: String?,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // 当IME隐藏时，取消截获焦点
    val imeHostState = LocalTileImeHostState.current
    val visibleFlow = snapshotFlow { imeHostState.visible }
    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            visibleFlow.collect { visible ->
                if (!visible) {
                    focusRequester.freeFocus()
                }
            }
        }

        onDispose {
            job.cancel()
        }
    }

    FlowRow(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable(interactionSource = state.interactionSource)
            .tapPress(state.interactionSource) {
                focusRequester.requestFocus()
            }
    ) {
        Tiles(value, fontSize = fontSizeInSp.sp)
    }
}
