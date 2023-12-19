package io.ssttkkl.mahjongutils.app.components.tiles

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
import io.ssttkkl.mahjongutils.app.utils.emoji
import io.ssttkkl.mahjongutils.app.utils.emojiToTile
import mahjongutils.models.Tile as TileModel

private val tileContentIdMapping = TileModel.all.associateBy {
    "tile-${it}"
}

private val tileContentIdRevMapping = tileContentIdMapping.entries.associate {
    it.value to it.key
}

private fun annotateTileFromEmoji(string: String): AnnotatedString = buildAnnotatedString {
    val patterns = TileModel.all.map { it.emoji }.toSet()

    var i = 0
    while (i < string.length) {
        if (i + 1 < string.length) {
            val span = string.substring(i, i + 2)
            if (span in patterns) {
                val tile = emojiToTile(span)
                appendInlineContent(tileContentIdRevMapping[tile]!!, span)
                i += 2
                continue
            }
        }

        append(string[i])
        i += 1
    }
}

private val tileInlineTextContent = TileModel.all.associate { tile ->
    tileContentIdRevMapping[tile]!! to InlineTextContent(
        Placeholder(
            (1 * 1.6).em, (1.4 * 1.6).em, // 牌图片的比例是1.6:1
            PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Tile(tile)
    }
}

@Composable
fun TileInlineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
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
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = annotateTileFromEmoji(text),
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