package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 30.sp,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val tileImeHostState = LocalTileImeHostState.current

    val coroutineScope = rememberCoroutineScope()
    val state = remember { CoreTileFieldState() }

    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val consumer = remember(tileImeHostState) { tileImeHostState.TileImeConsumer() }

    // 退出时隐藏键盘
    DisposableEffect(tileImeHostState) {
        onDispose {
            consumer.release()
        }
    }

    // 限制selection在值范围内
    state.selection = state.selection.coerceIn(0, value.size)

    // 绑定键盘到该输入框
    DisposableEffect(tileImeHostState, enabled, state.focused) {
        if (enabled && state.focused) {
            consumer.consume()

            val collectPendingTileJob = coroutineScope.launch {
                tileImeHostState.pendingTile.collect { tile ->
                    val newValue = buildList {
                        addAll(currentValue.take(state.selection.start))
                        add(tile)
                        addAll(currentValue.takeLast(currentValue.size - state.selection.end))
                    }
                    currentOnValueChange(newValue)
                    state.selection = TextRange(state.selection.start + 1)
                }
            }
            val collectBackspaceJob = coroutineScope.launch {
                tileImeHostState.backspace.collect {
                    if (state.selection.length == 0) {
                        val curCursor = state.selection.start
                        if (curCursor - 1 in currentValue.indices) {
                            val newValue = ArrayList(currentValue).apply {
                                removeAt(curCursor - 1)
                            }
                            currentOnValueChange(newValue)
                            state.selection = TextRange(curCursor - 1)
                        }
                    } else {
                        val newValue = buildList {
                            addAll(currentValue.take(state.selection.start))
                            addAll(currentValue.takeLast(currentValue.size - state.selection.end))
                        }
                        currentOnValueChange(newValue)
                        state.selection = TextRange(state.selection.start)
                    }
                }
            }
            val collectCollapseJob = coroutineScope.launch {
                tileImeHostState.collapse.collect {
                    consumer.release()
                }
            }

            onDispose {
                collectPendingTileJob.cancel()
                collectBackspaceJob.cancel()
                collectCollapseJob.cancel()
            }
        } else {
            consumer.release()
            onDispose { }
        }
    }

    val cursorColor: Color = MaterialTheme.colorScheme.primary
    val errorCursorColor: Color = MaterialTheme.colorScheme.error

    val shape = OutlinedTextFieldDefaults.shape
    val colors = OutlinedTextFieldDefaults.colors()

    val decorationBox = @Composable { innerTextField: @Composable () -> Unit ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = "",
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            singleLine = true,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled,
                    isError,
                    interactionSource,
                    colors,
                    shape
                )
            }
        )
    }

    decorationBox {
        CoreTileField(
            value = value,
            modifier = modifier,
            state = state,
            cursorColor = if (isError) errorCursorColor else cursorColor,
            fontSizeInSp = if (fontSize.isSp)
                fontSize.value
            else
                LocalTextStyle.current.fontSize.value
        )
    }
}


@Composable
fun TileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    BaseTileField(
        value,
        onValueChange,
        modifier,
        trailingIcon = {
            Text(
                stringResource(MR.strings.text_tiles_num_short, value.size),
                style = MaterialTheme.typography.labelMedium
            )
        },
        isError = isError
    )
}