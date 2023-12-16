package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.utils.Spacing

interface KeyboardKeyItem {
    val weightOfRow: Float
        get() = 1f

    @Composable
    fun display(onClick: () -> Unit)
}

@Composable
fun <T : KeyboardKeyItem> KeyboardScreen(
    keysMatrix: List<List<T>>,
    onCollapse: () -> Unit,
    onCommit: (T) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.4f))
            .padding(bottom = Spacing.large)
    ) {
        Image(
            Icons.Filled.KeyboardArrowDown,
            Res.string.label_keyboard_collapse,
            Modifier.clickable {
                onCollapse()
            }.fillMaxWidth()
                .padding(Spacing.small)
                .height(24.dp),
            alignment = Alignment.Center,
        )

        Divider(Modifier.padding(bottom = Spacing.medium))

        keysMatrix.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium)) {
                row.forEach { key ->
                    KeyboardKey(
                        Modifier.weight(key.weightOfRow).height(56.dp),
                        {
                            onCommit(key)
                        }
                    ) {
                        key.display(it)
                    }
                }
            }
        }
    }
}

@Composable
fun FixedHeightBox(modifier: Modifier, height: Dp, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        val h = height.roundToPx()
        layout(constraints.maxWidth, h) {
            placeables.forEach { placeable ->
                placeable.place(x = 0, y = kotlin.math.min(0, h - placeable.height))
            }
        }
    }
}

@Composable
fun KeyboardKey(
    modifier: Modifier,
    onPress: () -> Unit,
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        content(onPress)
    }
}
