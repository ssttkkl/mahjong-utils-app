package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import io.ssttkkl.mahjongutils.app.base.components.AutoSingleLineText
import io.ssttkkl.mahjongutils.app.base.components.TextSizeConstrainedResult
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.utils.emoji
import io.ssttkkl.mahjongutils.app.utils.emojiToTile
import mahjongutils.models.Tile
import mahjongutils.models.Tile as TileModel

private val tileContentIdMapping = TileModel.all.associateBy {
    "tile-${it}"
}

private val tileContentIdRevMapping = tileContentIdMapping.entries.associate {
    it.value to it.key
}

private const val tileBackContentId = "tile-back"

/** The annotation tag used by inline content. */
internal const val INLINE_CONTENT_TAG = "androidx.compose.foundation.text.inlineContent"

private fun AnnotatedString.getTiles(): List<Tile?> {
    val str = this
    return buildList {
        str.getStringAnnotations(0, str.length).forEach {
            if (it.tag != INLINE_CONTENT_TAG) {
                return@forEach
            }

            if (tileContentIdMapping.containsKey(it.item)) {
                add(tileContentIdMapping[it.item]!!)
            } else if (tileBackContentId == it.item) {
                add(null)
            }
        }
    }
}

fun Tile?.annotatedAsInline(
    fontSize: TextUnit = TextUnit.Unspecified
): AnnotatedString = buildAnnotatedString {
    val tile = this@annotatedAsInline
    pushStyle(SpanStyle(fontSize = fontSize))
    appendInlineContent(
        tileContentIdRevMapping[tile] ?: tileBackContentId,
        tile?.toString() ?: "0z"
    )
    pop()
}

fun Iterable<Tile?>.annotatedAsInline(
    fontSize: TextUnit = TextUnit.Unspecified
): AnnotatedString =
    buildAnnotatedString {
        forEach {
            append(it.annotatedAsInline(fontSize))
        }
    }

fun tileBackInline(
    fontSize: TextUnit = TextUnit.Unspecified
): AnnotatedString = (null as Tile?).annotatedAsInline(fontSize)

private fun annotateTileFromEmoji(
    charSeq: CharSequence,
    fontSize: TextUnit = TextUnit.Unspecified,
): AnnotatedString = buildAnnotatedString {
    val patterns = TileModel.all.map { it.emoji }.toSet()

    var i = 0
    while (i < charSeq.length) {
        if (i + 1 < charSeq.length) {
            val span = charSeq.substring(i, i + 2)
            if (span in patterns) {
                val tile = emojiToTile(span)
                append(tile.annotatedAsInline(fontSize))
                i += 2
                continue
            }
        }

        append(charSeq[i])
        i += 1
    }

    if (charSeq is AnnotatedString) {
        charSeq.getStringAnnotations(0, charSeq.length).forEach {
            addStringAnnotation(it.tag, it.item, it.start, it.end)
        }
        charSeq.spanStyles.forEach {
            addStyle(it.item, it.start, it.end)
        }
        charSeq.paragraphStyles.forEach {
            addStyle(it.item, it.start, it.end)
        }
    }
}

@Composable
fun MeasureViewSize(
    viewToMeasure: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (DpSize) -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val measuredSize = subcompose("viewToMeasure") {
            viewToMeasure()
        }[0].measure(constraints)
            .let {
                DpSize(
                    width = it.width.toDp(),
                    height = it.height.toDp()
                )
            }

        val contentPlaceable = subcompose("content") {
            content(measuredSize)
        }.firstOrNull()?.measure(constraints)

        layout(contentPlaceable?.width ?: 0, contentPlaceable?.height ?: 0) {
            contentPlaceable?.place(0, 0)
        }
    }
}

@Composable
private fun getTileInlineTextContent(
    tile: Tile?,
    tileImage: @Composable (Tile?) -> Unit,
    ratio: Float,
): InlineTextContent {
    val width: TextUnit
    val height: TextUnit

    if (ratio > 1) {
        width = 1.em
        height = ratio.em
    } else {
        width = (1 / ratio).em
        height = 1.em
    }

    return remember(tile, tileImage, ratio) {
        InlineTextContent(
            Placeholder(
                width, height,
                PlaceholderVerticalAlign.TextCenter
            )
        ) {
            tileImage(tile)
        }
    }
}

@Composable
private fun tileInlineTextContent(
    tileImage: @Composable (Tile?) -> Unit,
    ratio: Map<Tile?, Float>,
): Map<String, InlineTextContent> {
    return buildMap {
        TileModel.all.forEach { tile ->
            put(
                tileContentIdRevMapping[tile]!!,
                getTileInlineTextContent(tile, tileImage, ratio[tile] ?: 1f)
            )
        }

        put(
            tileBackContentId,
            getTileInlineTextContent(null, tileImage, ratio[null] ?: 1f)
        )
    }
}

@Composable
private fun MeasureTileImageRatio(
    tileSet: Set<Tile?>,
    modifier: Modifier = Modifier,
    tileImage: @Composable (Tile?) -> Unit = { TileImage(it) },
    content: @Composable (Map<Tile?, Float>) -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val measuredTileRatio = tileSet.associate {
            val measurable = subcompose(it) { tileImage(it) }
            it to (measurable.firstOrNull()
                ?.measure(constraints)?.let {
                    it.height.toFloat() / it.width
                } ?: 1f)
        }

        val contentPlaceable = subcompose("content") {
            content(measuredTileRatio)
        }.firstOrNull()?.measure(constraints)

        layout(contentPlaceable?.width ?: 0, contentPlaceable?.height ?: 0) {
            contentPlaceable?.place(0, 0)
        }
    }
}

@Composable
fun TileInlineText(
    text: CharSequence,
    modifier: Modifier = Modifier,
    tileImage: @Composable (Tile?) -> Unit = { TileImage(it) },
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    tileSize: TextUnit = LocalTileTextSize.current,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val text = annotateTileFromEmoji(text, tileSize)
    val tileSet = text.getTiles().toSet()
    MeasureTileImageRatio(tileSet, modifier, tileImage) { measuredTileRatio ->
        Text(
            text = text,
            inlineContent = tileInlineTextContent(tileImage, measuredTileRatio),
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
}

@Composable
fun TileInlineAutoSingleLineText(
    text: CharSequence,
    modifier: Modifier = Modifier,
    tileImage: @Composable (Tile?) -> Unit = { TileImage(it) },
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    tileSize: TextUnit = LocalTileTextSize.current,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    onTextSizeConstrained: ((TextSizeConstrainedResult) -> Unit)? = null
) {
    val text = annotateTileFromEmoji(text, tileSize)
    val tileSet = text.getTiles().toSet()
    MeasureTileImageRatio(tileSet, modifier, tileImage) { measuredTileRatio ->
        AutoSingleLineText(
            text = text,
            inlineContent = tileInlineTextContent(tileImage, measuredTileRatio),
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            style = style,
            onTextSizeConstrained = onTextSizeConstrained
        )
    }
}