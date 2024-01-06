package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Caption(
    title: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    Row {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            )
        ) {
            title?.invoke()
        }

        if (content != null) {
            Spacer(Modifier.width(8.dp))

            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelLarge
            ) {
                content.invoke()
            }
        }
    }
}