package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.tile.FuroTiles
import io.ssttkkl.mahjongutils.app.components.tile.RotatedSingleTile
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HoraTiles(args: HoraArgs) {
    FlowRow {
        Row {
            Tiles(args.tiles.dropLast(1))
            RotatedSingleTile(args.tiles.last())
        }

        args.furo.forEach {
            Spacer(Modifier.width(8.dp))
            FuroTiles(it)
        }
    }
}