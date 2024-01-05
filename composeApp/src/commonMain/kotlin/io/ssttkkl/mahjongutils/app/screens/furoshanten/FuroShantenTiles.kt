package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.tile.TileInlineText
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.utils.emoji
import mahjongutils.models.Tile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuroShantenTiles(tiles: List<Tile>, chanceTile: Tile) {
    FlowRow {
        Tiles(tiles)

        Spacer(Modifier.width(8.dp))

        TileInlineText(
            stringResource(
                MR.strings.label_tile_discarded_by_other_short,
                chanceTile.emoji
            )
        )
    }
}