package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.tile.TileImage
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.utils.percentile
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_tiles_num
import mahjongutils.composeapp.generated.resources.text_tiles_num_and_percentile
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.stringResource

@Composable
private fun TilesNumCaption(tileNum: Int, percentile: Double? = null) {
    if (percentile != null) {
        Text(
            stringResource(
                Res.string.text_tiles_num_and_percentile,
                tileNum,
                percentile.percentile()
            )
        )
    } else {
        Text(stringResource(Res.string.text_tiles_num, tileNum))
    }
}

@Composable
fun TilesWithNumTopCardPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int,
    percentile: Double? = null,
    tileImage: @Composable (Tile) -> Unit = { TileImage(it) },
) {
    TopCardPanel(
        { Text(label) },
        caption = { TilesNumCaption(tileNum, percentile) }
    ) {
        if (tiles.isNotEmpty()) {
            Tiles(
                tiles.sorted(),
                tileImage = {
                    it?.let { tileImage(it) }
                },
            )
        }
    }
}


@Composable
fun TilesWithNumPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int,
    percentile: Double? = null,
    tileImage: @Composable (Tile) -> Unit = { TileImage(it) },
) {
    Panel(
        { Text(label) },
        caption = { TilesNumCaption(tileNum, percentile) }
    ) {
        if (tiles.isNotEmpty()) {
            Tiles(
                tiles.sorted(),
                tileImage = {
                    it?.let { tileImage(it) }
                },
            )
        }
    }
}