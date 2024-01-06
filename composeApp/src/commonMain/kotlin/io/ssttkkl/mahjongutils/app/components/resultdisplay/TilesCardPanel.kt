package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.utils.percentile
import mahjongutils.models.Tile

@Composable
private fun TilesNumCaption(tileNum: Int, percentile: Double? = null) {
    if (percentile != null) {
        Text(
            stringResource(
                MR.strings.text_tiles_num_and_percentile,
                tileNum,
                percentile.percentile()
            )
        )
    } else {
        Text(stringResource(MR.strings.text_tiles_num, tileNum))
    }
}

@Composable
fun TilesWithNumTopCardPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int,
    percentile: Double? = null
) {
    TopCardPanel(
        { Text(label) },
        caption = { TilesNumCaption(tileNum, percentile) }
    ) {
        if (tiles.isNotEmpty()) {
            Tiles(tiles.sorted())
        }
    }
}


@Composable
fun TilesWithNumPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int,
    percentile: Double? = null
) {
    Panel(
        { Text(label) },
        caption = { TilesNumCaption(tileNum, percentile) }
    ) {
        if (tiles.isNotEmpty()) {
            Tiles(tiles.sorted())
        }
    }
}