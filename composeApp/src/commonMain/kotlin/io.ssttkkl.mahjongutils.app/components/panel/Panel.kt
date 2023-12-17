package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.Spacing

@Composable
fun Panel(
    header: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            header,
            modifier = Modifier.padding(vertical = Spacing.large, horizontal = Spacing.medium),
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold)
        )
        content()
        Spacer(Modifier.height(Spacing.large))
    }
}

@Composable
fun CardPanel(
    header: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(modifier, elevation = 4.dp) {
        Column {
            Panel(header) {
                content()
            }
        }
    }
}