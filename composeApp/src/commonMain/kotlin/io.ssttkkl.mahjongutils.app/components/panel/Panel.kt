package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.Spacing

@Composable
fun Panel(
    header: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            header,
            titleModifier,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
fun TopPanel(
    header: String,
    noPaddingContent: Boolean = false,
    content: @Composable () -> Unit
) {
    with(Spacing.current) {
        if (!noPaddingContent) {
            Panel(header, Modifier.fillMaxWidth().windowHorizontalMargin()) { content() }
        } else {
            Panel(
                header,
                Modifier.fillMaxWidth(),
                Modifier.windowHorizontalMargin()
            ) { content() }
        }
    }
}

@Composable
fun CardPanel(
    header: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    with(Spacing.current) {
        Card(modifier) {
            Panel(header, Modifier.padding(cardInnerPadding), titleModifier) {
                content()
            }
        }
    }
}

@Composable
fun TopCardPanel(
    header: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    with(Spacing.current) {
        CardPanel(
            header,
            modifier.fillMaxWidth().windowHorizontalMargin(),
            titleModifier,
            content
        )
    }
}