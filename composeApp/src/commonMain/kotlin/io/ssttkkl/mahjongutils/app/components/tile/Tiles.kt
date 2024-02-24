package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.Image
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import io.ssttkkl.mahjongutils.app.components.autosizetext.TextSizeConstrainedResult
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.tile_1m
import mahjongutils.composeapp.generated.resources.tile_1m_lie
import mahjongutils.composeapp.generated.resources.tile_1p
import mahjongutils.composeapp.generated.resources.tile_1p_lie
import mahjongutils.composeapp.generated.resources.tile_1s
import mahjongutils.composeapp.generated.resources.tile_1s_lie
import mahjongutils.composeapp.generated.resources.tile_1z
import mahjongutils.composeapp.generated.resources.tile_1z_lie
import mahjongutils.composeapp.generated.resources.tile_2m
import mahjongutils.composeapp.generated.resources.tile_2m_lie
import mahjongutils.composeapp.generated.resources.tile_2p
import mahjongutils.composeapp.generated.resources.tile_2p_lie
import mahjongutils.composeapp.generated.resources.tile_2s
import mahjongutils.composeapp.generated.resources.tile_2s_lie
import mahjongutils.composeapp.generated.resources.tile_2z
import mahjongutils.composeapp.generated.resources.tile_2z_lie
import mahjongutils.composeapp.generated.resources.tile_3m
import mahjongutils.composeapp.generated.resources.tile_3m_lie
import mahjongutils.composeapp.generated.resources.tile_3p
import mahjongutils.composeapp.generated.resources.tile_3p_lie
import mahjongutils.composeapp.generated.resources.tile_3s
import mahjongutils.composeapp.generated.resources.tile_3s_lie
import mahjongutils.composeapp.generated.resources.tile_3z
import mahjongutils.composeapp.generated.resources.tile_3z_lie
import mahjongutils.composeapp.generated.resources.tile_4m
import mahjongutils.composeapp.generated.resources.tile_4m_lie
import mahjongutils.composeapp.generated.resources.tile_4p
import mahjongutils.composeapp.generated.resources.tile_4p_lie
import mahjongutils.composeapp.generated.resources.tile_4s
import mahjongutils.composeapp.generated.resources.tile_4s_lie
import mahjongutils.composeapp.generated.resources.tile_4z
import mahjongutils.composeapp.generated.resources.tile_4z_lie
import mahjongutils.composeapp.generated.resources.tile_5m
import mahjongutils.composeapp.generated.resources.tile_5m_lie
import mahjongutils.composeapp.generated.resources.tile_5p
import mahjongutils.composeapp.generated.resources.tile_5p_lie
import mahjongutils.composeapp.generated.resources.tile_5s
import mahjongutils.composeapp.generated.resources.tile_5s_lie
import mahjongutils.composeapp.generated.resources.tile_5z
import mahjongutils.composeapp.generated.resources.tile_5z_lie
import mahjongutils.composeapp.generated.resources.tile_6m
import mahjongutils.composeapp.generated.resources.tile_6m_lie
import mahjongutils.composeapp.generated.resources.tile_6p
import mahjongutils.composeapp.generated.resources.tile_6p_lie
import mahjongutils.composeapp.generated.resources.tile_6s
import mahjongutils.composeapp.generated.resources.tile_6s_lie
import mahjongutils.composeapp.generated.resources.tile_6z
import mahjongutils.composeapp.generated.resources.tile_6z_lie
import mahjongutils.composeapp.generated.resources.tile_7m
import mahjongutils.composeapp.generated.resources.tile_7m_lie
import mahjongutils.composeapp.generated.resources.tile_7p
import mahjongutils.composeapp.generated.resources.tile_7p_lie
import mahjongutils.composeapp.generated.resources.tile_7s
import mahjongutils.composeapp.generated.resources.tile_7s_lie
import mahjongutils.composeapp.generated.resources.tile_7z
import mahjongutils.composeapp.generated.resources.tile_7z_lie
import mahjongutils.composeapp.generated.resources.tile_8m
import mahjongutils.composeapp.generated.resources.tile_8m_lie
import mahjongutils.composeapp.generated.resources.tile_8p
import mahjongutils.composeapp.generated.resources.tile_8p_lie
import mahjongutils.composeapp.generated.resources.tile_8s
import mahjongutils.composeapp.generated.resources.tile_8s_lie
import mahjongutils.composeapp.generated.resources.tile_9m
import mahjongutils.composeapp.generated.resources.tile_9m_lie
import mahjongutils.composeapp.generated.resources.tile_9p
import mahjongutils.composeapp.generated.resources.tile_9p_lie
import mahjongutils.composeapp.generated.resources.tile_9s
import mahjongutils.composeapp.generated.resources.tile_9s_lie
import mahjongutils.composeapp.generated.resources.tile_back
import mahjongutils.composeapp.generated.resources.tile_back_lie
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun TileImage(
    tile: Tile?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        tile?.painterResource ?: painterResource(Res.drawable.tile_back),
        tile.toString(),
        modifier,
        alignment, contentScale, alpha, colorFilter
    )
}

@Composable
fun LieDownTileImage(
    tile: Tile?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        tile?.liePainterResource ?: painterResource(Res.drawable.tile_back_lie),
        tile.toString(),
        modifier,
        alignment, contentScale, alpha, colorFilter
    )
}

@Composable
fun Tiles(
    tiles: Iterable<Tile>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = LocalTileTextSize.current,
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
    TileInlineText(
        text = tiles.annotatedAsInline(),
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
        style = style
    )
}

@Composable
fun LieDownTiles(
    tiles: Iterable<Tile>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = LocalTileTextSize.current,
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
    LieDownTileInlineText(
        text = tiles.annotatedAsInline(),
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
        style = style
    )
}

@Composable
fun AutoSingleLineTiles(
    tiles: Iterable<Tile>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = LocalTileTextSize.current,
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
    TileInlineAutoSingleLineText(
        text = tiles.annotatedAsInline(),
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


private val tileToImgResMapping = buildMap {
    this[Tile["1m"]] = Res.drawable.tile_1m
    this[Tile["2m"]] = Res.drawable.tile_2m
    this[Tile["3m"]] = Res.drawable.tile_3m
    this[Tile["4m"]] = Res.drawable.tile_4m
    this[Tile["5m"]] = Res.drawable.tile_5m
    this[Tile["6m"]] = Res.drawable.tile_6m
    this[Tile["7m"]] = Res.drawable.tile_7m
    this[Tile["8m"]] = Res.drawable.tile_8m
    this[Tile["9m"]] = Res.drawable.tile_9m

    this[Tile["1p"]] = Res.drawable.tile_1p
    this[Tile["2p"]] = Res.drawable.tile_2p
    this[Tile["3p"]] = Res.drawable.tile_3p
    this[Tile["4p"]] = Res.drawable.tile_4p
    this[Tile["5p"]] = Res.drawable.tile_5p
    this[Tile["6p"]] = Res.drawable.tile_6p
    this[Tile["7p"]] = Res.drawable.tile_7p
    this[Tile["8p"]] = Res.drawable.tile_8p
    this[Tile["9p"]] = Res.drawable.tile_9p

    this[Tile["1s"]] = Res.drawable.tile_1s
    this[Tile["2s"]] = Res.drawable.tile_2s
    this[Tile["3s"]] = Res.drawable.tile_3s
    this[Tile["4s"]] = Res.drawable.tile_4s
    this[Tile["5s"]] = Res.drawable.tile_5s
    this[Tile["6s"]] = Res.drawable.tile_6s
    this[Tile["7s"]] = Res.drawable.tile_7s
    this[Tile["8s"]] = Res.drawable.tile_8s
    this[Tile["9s"]] = Res.drawable.tile_9s

    this[Tile["1z"]] = Res.drawable.tile_1z
    this[Tile["2z"]] = Res.drawable.tile_2z
    this[Tile["3z"]] = Res.drawable.tile_3z
    this[Tile["4z"]] = Res.drawable.tile_4z
    this[Tile["5z"]] = Res.drawable.tile_5z
    this[Tile["6z"]] = Res.drawable.tile_6z
    this[Tile["7z"]] = Res.drawable.tile_7z
}

private val tileToLieImgResMapping = buildMap {
    this[Tile["1m"]] = Res.drawable.tile_1m_lie
    this[Tile["2m"]] = Res.drawable.tile_2m_lie
    this[Tile["3m"]] = Res.drawable.tile_3m_lie
    this[Tile["4m"]] = Res.drawable.tile_4m_lie
    this[Tile["5m"]] = Res.drawable.tile_5m_lie
    this[Tile["6m"]] = Res.drawable.tile_6m_lie
    this[Tile["7m"]] = Res.drawable.tile_7m_lie
    this[Tile["8m"]] = Res.drawable.tile_8m_lie
    this[Tile["9m"]] = Res.drawable.tile_9m_lie

    this[Tile["1p"]] = Res.drawable.tile_1p_lie
    this[Tile["2p"]] = Res.drawable.tile_2p_lie
    this[Tile["3p"]] = Res.drawable.tile_3p_lie
    this[Tile["4p"]] = Res.drawable.tile_4p_lie
    this[Tile["5p"]] = Res.drawable.tile_5p_lie
    this[Tile["6p"]] = Res.drawable.tile_6p_lie
    this[Tile["7p"]] = Res.drawable.tile_7p_lie
    this[Tile["8p"]] = Res.drawable.tile_8p_lie
    this[Tile["9p"]] = Res.drawable.tile_9p_lie

    this[Tile["1s"]] = Res.drawable.tile_1s_lie
    this[Tile["2s"]] = Res.drawable.tile_2s_lie
    this[Tile["3s"]] = Res.drawable.tile_3s_lie
    this[Tile["4s"]] = Res.drawable.tile_4s_lie
    this[Tile["5s"]] = Res.drawable.tile_5s_lie
    this[Tile["6s"]] = Res.drawable.tile_6s_lie
    this[Tile["7s"]] = Res.drawable.tile_7s_lie
    this[Tile["8s"]] = Res.drawable.tile_8s_lie
    this[Tile["9s"]] = Res.drawable.tile_9s_lie

    this[Tile["1z"]] = Res.drawable.tile_1z_lie
    this[Tile["2z"]] = Res.drawable.tile_2z_lie
    this[Tile["3z"]] = Res.drawable.tile_3z_lie
    this[Tile["4z"]] = Res.drawable.tile_4z_lie
    this[Tile["5z"]] = Res.drawable.tile_5z_lie
    this[Tile["6z"]] = Res.drawable.tile_6z_lie
    this[Tile["7z"]] = Res.drawable.tile_7z_lie
}

val Tile.drawableResource: DrawableResource
    get() = tileToImgResMapping[this] ?: Res.drawable.tile_back

val Tile.lieDrawableResource: DrawableResource
    get() = tileToLieImgResMapping[this] ?: Res.drawable.tile_back_lie

val Tile.painterResource: Painter
    @Composable
    get() = painterResource(drawableResource)

val Tile.liePainterResource: Painter
    @Composable
    get() = painterResource(lieDrawableResource)
