package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import io.ssttkkl.mahjongutils.app.components.autosizetext.AutoSingleLineText
import io.ssttkkl.mahjongutils.app.components.autosizetext.TextSizeConstrainedResult
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

fun Tile.annotatedAsInline(
    fontSize: TextUnit = TextUnit.Unspecified
): AnnotatedString = buildAnnotatedString {
    val tile = this@annotatedAsInline
    pushStyle(SpanStyle(fontSize = fontSize))
    appendInlineContent(tileContentIdRevMapping[tile]!!, tile.toString())
    pop()
}

fun Iterable<Tile>.annotatedAsInline(
    fontSize: TextUnit = TextUnit.Unspecified
): AnnotatedString =
    buildAnnotatedString {
        forEach {
            append(it.annotatedAsInline(fontSize))
        }
    }

fun tileBackInline(
    fontSize: TextUnit = TextUnit.Unspecified
): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontSize = fontSize))
    appendInlineContent(tileBackContentId, "0z")
    pop()
}

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

private val tileInlineTextContent = TileModel.all.associate { tile ->
    tileContentIdRevMapping[tile]!! to InlineTextContent(
        Placeholder(
            1.em, 1.4.em, // 牌图片的比例是1.4:1
            PlaceholderVerticalAlign.TextCenter
        )
    ) {
        TileImage(tile)
    }
} + Pair(tileBackContentId, InlineTextContent(
    Placeholder(
        1.em, 1.4.em, // 牌图片的比例是1.4:1
        PlaceholderVerticalAlign.TextCenter
    )
) {
    TileImage(null)
})

private val lieDownTileInlineTextContent = TileModel.all.associate { tile ->
    tileContentIdRevMapping[tile]!! to InlineTextContent(
        Placeholder(
            1.4.em, 1.em, // 牌图片的比例是1.4:1
            PlaceholderVerticalAlign.TextBottom
        )
    ) {
        LieDownTileImage(tile)
    }
} + Pair(tileBackContentId, InlineTextContent(
    Placeholder(
        1.4.em, 1.em, // 牌图片的比例是1.4:1
        PlaceholderVerticalAlign.TextBottom
    )
) {
    LieDownTileImage(null)
})

@Composable
fun TileInlineText(
    text: CharSequence,
    modifier: Modifier = Modifier,
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
    Text(
        text = annotateTileFromEmoji(text, tileSize),
        inlineContent = tileInlineTextContent,
        modifier = modifier,
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

@Composable
fun LieDownTileInlineText(
    text: CharSequence,
    modifier: Modifier = Modifier,
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
    Text(
        text = annotateTileFromEmoji(text, tileSize),
        inlineContent = lieDownTileInlineTextContent,
        modifier = modifier,
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

@Composable
fun TileInlineAutoSingleLineText(
    text: CharSequence,
    modifier: Modifier = Modifier,
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
    AutoSingleLineText(
        text = annotateTileFromEmoji(text, tileSize),
        inlineContent = tileInlineTextContent,
        modifier = modifier,
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