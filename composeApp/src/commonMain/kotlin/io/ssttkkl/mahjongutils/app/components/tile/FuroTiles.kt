package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import mahjongutils.models.Chi
import mahjongutils.models.Furo
import mahjongutils.models.Kan
import mahjongutils.models.Pon

@Composable
fun FuroTiles(
    furo: Furo, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current
) {
    when (furo) {
        is Chi -> ChiTiles(furo, modifier, fontSize)
        is Pon -> PonTiles(furo, modifier, fontSize)
        is Kan -> {
            if (furo.ankan) {
                AnkanTiles(furo, modifier, fontSize)
            } else {
                MinkanTiles(furo, modifier, fontSize)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChiTiles(
    chi: Chi, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        RotatedSingleTile(chi.tile, fontSize = fontSize)
        Tiles(chi.tiles - chi.tile, fontSize = fontSize)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PonTiles(
    pon: Pon, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        RotatedSingleTile(pon.tile, fontSize = fontSize)
        Tiles(listOf(pon.tile, pon.tile), fontSize = fontSize)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MinkanTiles(
    kan: Kan, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        RotatedSingleTile(kan.tile, fontSize = fontSize)
        Tiles(listOf(kan.tile, kan.tile, kan.tile), fontSize = fontSize)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnkanTiles(
    kan: Kan, modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTileTextSize.current,
) {
    FlowRow(modifier) {
        TileInlineText(
            tileBackInline() + listOf(kan.tile, kan.tile).annotatedAsInline() + tileBackInline(),
            fontSize = fontSize
        )
    }
}