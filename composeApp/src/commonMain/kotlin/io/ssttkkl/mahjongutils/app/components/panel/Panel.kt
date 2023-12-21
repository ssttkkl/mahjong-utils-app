package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
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
private fun PanelTitle(
    header: @Composable () -> String,
    modifier: Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(modifier) {
        Text(
            header(),
            Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        trailingContent?.let {
            Spacer(Modifier.padding(start = 8.dp))
            trailingContent()
        }
    }
}

@Composable
fun Panel(
    header: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    titleTrailingContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        PanelTitle({ header }, titleModifier, titleTrailingContent)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
fun TopPanel(
    header: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    noPaddingContent: Boolean = false,
    titleTrailingContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    with(Spacing.current) {
        if (!noPaddingContent) {
            Panel(
                header,
                Modifier.fillMaxWidth().windowHorizontalMargin().then(modifier),
                titleModifier,
                titleTrailingContent = titleTrailingContent
            ) { content() }
        } else {
            Panel(
                header,
                Modifier.fillMaxWidth().then(modifier),
                Modifier.windowHorizontalMargin().then(titleModifier),
                titleTrailingContent = titleTrailingContent
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
    titleTrailingContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    with(Spacing.current) {
        Panel(header, modifier, titleModifier, titleTrailingContent) {
            Card(Modifier.fillMaxWidth().then(cardModifier)) {
                Box(Modifier.padding(cardInnerPadding)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun TopCardPanel(
    header: String,
    titleTrailingContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
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
            titleTrailingContent = titleTrailingContent,
            content = content
        )
    }
}

fun <T> LazyListScope.LazyCardPanel(
    items: Sequence<T>,
    keyMapping: (key: T) -> Any? = { it },
    header: @Composable () -> String,
    cardModifier: @Composable (key: T) -> Modifier = { Modifier },
    titleModifier: @Composable () -> Modifier = { Modifier },
    titleTrailingContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.(T) -> Unit
) {
    item {
        PanelTitle(header, titleModifier(), titleTrailingContent)
        Spacer(Modifier.height(8.dp))
    }

    items.forEachIndexed { index, it ->
        item(keyMapping(it)) {
            if (index != 0) {
                Spacer(Modifier.height(4.dp))
            }

            with(Spacing.current) {
                Card(Modifier.fillMaxWidth().then(cardModifier(it))) {
                    Box(Modifier.padding(cardInnerPadding)) {
                        content(it)
                    }
                }
            }
        }
    }
}
