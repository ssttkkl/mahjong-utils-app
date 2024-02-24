package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onCommit: (T) -> Unit,
) {
    val windowSizeClass = LocalAppState.current.windowSizeClass

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
