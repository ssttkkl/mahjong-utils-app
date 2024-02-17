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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState

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
    val windowSizeClass = LocalAppState.current.windowSizeClass

    Column(
        Modifier.fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.4f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Filled.KeyboardArrowDown,
            "",
            Modifier.clickable {
                onCollapse()
            }.fillMaxWidth()
                .padding(4.dp)
                .height(24.dp),
            alignment = Alignment.Center,
        )

        Divider(Modifier.padding(bottom = 8.dp))

        Column(
            modifier = Modifier
                .run {
                    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                        this.widthIn(0.dp, 500.dp)
                    } else {
                        this.fillMaxWidth()
                    }
                }
                .padding(bottom = 8.dp)
        ) {
            keysMatrix.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
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
