package io.ssttkkl.mahjongutils.app.components.autosizetext

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Immutable
data class TextSizeConstrainedResult(
    val textSize: TextUnit,
    val height: Int
)

@Composable
fun AutoSingleLineText(
    text: AnnotatedString,
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
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    style: TextStyle = LocalTextStyle.current,
    onTextSizeConstrained: ((TextSizeConstrainedResult) -> Unit)? = null
) {
    val currentOnTextSizeConstrained by rememberUpdatedState(onTextSizeConstrained)

    var fontSize by remember {
        mutableStateOf(
            if (fontSize != TextUnit.Unspecified) {
                fontSize
            } else {
                style.fontSize
            }
        )
    }

    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        text = text,
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
        inlineContent = inlineContent,
        style = style,
        onTextLayout = { result ->
            if (result.multiParagraph.lineCount > 1) {
                fontSize *= 0.9f
            } else {
                currentOnTextSizeConstrained?.invoke(
                    TextSizeConstrainedResult(fontSize, result.size.height)
                )
                readyToDraw = true
            }
        }
    )
}

@Composable
fun AutoSingleLineText(
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
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    style: TextStyle = LocalTextStyle.current,
    onTextSizeConstrained: ((TextSizeConstrainedResult) -> Unit)? = null
) {
    AutoSingleLineText(
        modifier = modifier,
        text = AnnotatedString(text),
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
        inlineContent = inlineContent,
        style = style,
        onTextSizeConstrained = onTextSizeConstrained
    )
}