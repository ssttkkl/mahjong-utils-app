package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.ssttkkl.mahjongutils.app.utils.Spacing

@Composable
fun Panel(
    header: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            header,
            modifier = Modifier.padding(vertical = Spacing.large, horizontal = Spacing.medium),
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold)
        )

        content()
    }
}