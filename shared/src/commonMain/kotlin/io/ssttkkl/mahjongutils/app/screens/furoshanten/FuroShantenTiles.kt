package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.tile.AutoSingleLineTiles
import io.ssttkkl.mahjongutils.app.components.tile.TileInlineText
import io.ssttkkl.mahjongutils.app.base.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.base.utils.emoji
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_tile_discarded_by_other_short
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuroShantenTiles(tiles: List<Tile>, chanceTile: Tile) {
    FlowRow {
        val preferTileSize = LocalTileTextSize.current
        var reducedTileSize by remember { mutableStateOf(preferTileSize) }

        AutoSingleLineTiles(
            tiles,
            fontSize = preferTileSize
        ) { reducedTileSize = it.textSize }

        Spacer(Modifier.width(8.dp))

        TileInlineText(
            stringResource(
                Res.string.label_tile_discarded_by_other_short,
                chanceTile.emoji
            ),
            tileSize = reducedTileSize
        )
    }
}