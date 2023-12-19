package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.painterResource
import mahjongutils.models.Tile

@Composable
fun Tile(tile: Tile, modifier: Modifier = Modifier.height(30.dp)) {
    Image(
        tile.painterResource,
        tile.toString(),
        modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tiles(
    tiles: List<Tile>,
    modifier: Modifier = Modifier,
    tileModifier: Modifier = Modifier.height(30.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
) {
    FlowRow(modifier, horizontalArrangement, verticalArrangement, maxItemsInEachRow) {
        tiles.forEachIndexed { index, it ->
            Tile(it, tileModifier)
        }
    }
}