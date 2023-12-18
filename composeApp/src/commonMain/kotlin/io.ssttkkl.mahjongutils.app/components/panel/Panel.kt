package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.Spacing

@Composable
fun Panel(
    header: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        Text(
            header,
            titleModifier,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        Spacer(Modifier.height(8.dp))
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
    cardModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    vararg content: @Composable () -> Unit
) {
    with(Spacing.current) {
        Panel(header, modifier, titleModifier) {
            content.forEachIndexed { index, it ->
                Card(Modifier.fillMaxWidth().then(cardModifier)) {
                    Box(Modifier.padding(cardInnerPadding)) {
                        it()
                    }
                }

                if (index != content.size) {
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun TopCardPanel(
    header: String,
    vararg content: @Composable () -> Unit
) {
    with(Spacing.current) {
        CardPanel(
            header,
            Modifier.fillMaxWidth().windowHorizontalMargin(),
            titleModifier = Modifier.padding(
                start = cardInnerPadding.calculateStartPadding(
                    LocalLayoutDirection.current
                )
            ),
            content = content
        )
    }
}