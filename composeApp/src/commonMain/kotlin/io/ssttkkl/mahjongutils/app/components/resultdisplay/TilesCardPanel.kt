package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.CardPanel
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import mahjongutils.models.Tile

@Composable
fun TilesWithNumTopCardPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int
) {
    TopCardPanel(
        { Text(label) },
        caption = { Text(stringResource(MR.strings.text_tiles_num, tileNum)) }
    ) {
        Tiles(tiles.sorted())
    }
}

@Composable
fun TilesWithNumPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int
) {
    CardPanel(
        { Text(label) },
        caption = { Text(stringResource(MR.strings.text_tiles_num, tileNum)) }
    ) {
        Tiles(tiles.sorted())
    }
}