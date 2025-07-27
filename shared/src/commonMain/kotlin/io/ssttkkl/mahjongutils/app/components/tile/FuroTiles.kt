package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import mahjongutils.models.Furo
import mahjongutils.models.FuroType

@Composable
fun FuroTiles(
    furo: Furo, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current
) {
    when (furo.type) {
        FuroType.Chi -> ChiTiles(furo, modifier, fontSize)
        FuroType.Pon -> PonTiles(furo, modifier, fontSize)
        FuroType.Ankan -> AnkanTiles(furo, modifier, fontSize)
        FuroType.Kan -> MinkanTiles(furo, modifier, fontSize)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChiTiles(
    chi: Furo, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        Tiles(listOf(chi.tile),
            modifier = Modifier.align(Alignment.Bottom),
            fontSize = fontSize,
            tileImage = { LieDownTileImage(it) })
        Tiles(chi.tiles - chi.tile, fontSize = fontSize)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PonTiles(
    pon: Furo, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        Tiles(listOf(pon.tile),
            modifier = Modifier.align(Alignment.Bottom),
            fontSize = fontSize,
            tileImage = { LieDownTileImage(it) })
        Tiles(listOf(pon.tile, pon.tile), fontSize = fontSize)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MinkanTiles(
    kan: Furo, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        Tiles(listOf(kan.tile),
            modifier = Modifier.align(Alignment.Bottom),
            fontSize = fontSize,
            tileImage = { LieDownTileImage(it) })
        Tiles(listOf(kan.tile, kan.tile, kan.tile), fontSize = fontSize)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnkanTiles(
    kan: Furo, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        TileInlineText(
            tileBackInline() + listOf(kan.tile, kan.tile).annotatedAsInline() + tileBackInline(),
            modifier = Modifier.align(Alignment.Bottom),
            fontSize = fontSize
        )
    }
}