package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.tileime.LocalTileImeHostState
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import mahjongutils.models.Tile
import mahjongutils.models.toTilesString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BaseTileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyLarge,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val state = remember(interactionSource) {
        CoreTileFieldState(interactionSource)
    }
    val tileImeHostState = LocalTileImeHostState.current
    val consumer = remember(state, tileImeHostState) {
        TileFieldImeConsumer(state, tileImeHostState)
    }

    LaunchedEffect(value) {
        consumer.currentValue = value

        // 限制selection在值范围内
        state.selection = state.selection.coerceIn(0, value.size)
    }
    LaunchedEffect(onValueChange) {
        consumer.currentOnValueChange = onValueChange
    }

    // 退出时隐藏键盘
    DisposableEffect(consumer) {
        onDispose {
            consumer.stop()
        }
    }
    // 用户收起键盘后再点击输入框，重新弹出
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    LaunchedEffect(enabled && focused && pressed, consumer) {
        if (enabled && focused && pressed) {
            consumer.start()
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    // 绑定键盘到该输入框
    DisposableEffect(enabled && focused) {
        if (enabled && focused) {
            // 强制隐藏系统软键盘
            keyboardController?.hide()
            consumer.start()

            onDispose {
                consumer.stop()
            }
        } else {
            consumer.stop()
            onDispose { }
        }
    }

    val cursorColor: Color = MaterialTheme.colorScheme.primary
    val errorCursorColor: Color = MaterialTheme.colorScheme.error

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = TileTextSize.Default.bodyLarge,
    label: @Composable (() -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val shape = OutlinedTextFieldDefaults.shape
    val colors = OutlinedTextFieldDefaults.colors()

    val decorationBox = @Composable { innerTextField: @Composable () -> Unit ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value.toTilesString(),
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
            trailingIcon = {
                Text(
                    stringResource(MR.strings.text_tiles_num_short, value.size),
                    style = MaterialTheme.typography.labelMedium
                )
            },
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
        BaseTileField(
            value,
            onValueChange,
            modifier,
            enabled = enabled,
            fontSize = fontSize,
            isError = isError,
            interactionSource = interactionSource
        )
    }
}