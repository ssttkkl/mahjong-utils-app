package io.ssttkkl.mahjongutils.app.components.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.Spacing

@Composable
private fun PanelTitle(
    header: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        )
    ) {
        header()
    }
}

@Composable
private fun ColumnScope.PanelCaption(caption: @Composable ColumnScope.() -> Unit) {
    Spacer(Modifier.height(8.dp))

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.labelMedium) {
        caption()
    }
}

@Composable
fun Panel(
    header: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    caption: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        header?.let {
            PanelTitle(header)
            Spacer(Modifier.height(8.dp))
        }
        content()

        if (caption != null) {
            PanelCaption(caption)
        }
    }
}

@Composable
fun TopPanel(
    header: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    noContentPadding: Boolean = false,
    caption: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    with(Spacing.current) {
        if (!noContentPadding) {
            Panel(
                header,
                Modifier.fillMaxWidth().windowHorizontalMargin().then(modifier),
                caption = caption,
            ) { content() }
        } else {
            Panel(
                header?.let {
                    {
                        Surface(Modifier.windowHorizontalMargin()) {
                            header()
                        }
                    }
                },
                Modifier.fillMaxWidth().then(modifier),
                caption = caption
            ) { content() }
        }
    }
}

@Composable
fun CardPanel(
    header: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    cardInnerModifier: Modifier = Modifier.padding(Spacing.current.cardInnerPadding),
    caption: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Panel(header, modifier) {
        Card(Modifier.fillMaxWidth().then(cardModifier)) {
            Column(cardInnerModifier) {
                content()

                if (caption != null) {
                    PanelCaption(caption)
                }
            }
        }
    }
}

@Composable
fun TopCardPanel(
    header: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    caption: (@Composable ColumnScope.() -> Unit)? = null,
    noContentPadding: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    with(Spacing.current) {
        CardPanel(
            header = header,
            modifier = modifier.fillMaxWidth().windowHorizontalMargin(),
            cardInnerModifier = if (!noContentPadding)
                Modifier.padding(cardInnerPadding)
            else
                Modifier,
            caption = caption,
            content = content
        )
    }
}

fun <T> LazyListScope.LazyCardPanel(
    items: Sequence<T>,
    keyMapping: (key: T) -> Any? = { it },
    header: (@Composable () -> Unit)? = null,
    cardModifier: @Composable (key: T) -> Modifier = { Modifier },
    caption: (@Composable ColumnScope.(T) -> Unit)? = null,
    content: @Composable ColumnScope.(T) -> Unit
) {
    header?.let {
        item {
            PanelTitle(header)
            Spacer(Modifier.height(8.dp))
        }
    }

    items.forEachIndexed { index, it ->
        item(keyMapping(it)) {
            if (index != 0) {
                Spacer(Modifier.height(4.dp))
            }

            with(Spacing.current) {
                Card(Modifier.fillMaxWidth().then(cardModifier(it))) {
                    Column(Modifier.padding(cardInnerPadding)) {
                        content(it)

                        if (caption != null) {
                            PanelCaption { caption(it) }
                        }
                    }
                }
            }
        }
    }
}

fun <T> LazyListScope.LazyTopCardPanel(
    items: Sequence<T>,
    keyMapping: (key: T) -> Any? = { it },
    header: (@Composable () -> Unit)? = null,
    caption: (@Composable ColumnScope.(T) -> Unit)? = null,
    content: @Composable ColumnScope.(T) -> Unit
) {
    LazyCardPanel(
        items = items,
        keyMapping = keyMapping,
        header = header?.let {
            {
                with(Spacing.current) {
                    Surface(
                        Modifier.windowHorizontalMargin()
                    ) {
                        header()
                    }
                }
            }
        },
        cardModifier = {
            with(Spacing.current) {
                Modifier.windowHorizontalMargin()
            }
        },
        caption = caption,
        content = content
    )
}