package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.ssttkkl.mahjongutils.app.MR
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


private val tileToImgResMapping = buildMap {
    this[Tile["1m"]] = MR.images.tile_1m
    this[Tile["2m"]] = MR.images.tile_2m
    this[Tile["3m"]] = MR.images.tile_3m
    this[Tile["4m"]] = MR.images.tile_4m
    this[Tile["5m"]] = MR.images.tile_5m
    this[Tile["6m"]] = MR.images.tile_6m
    this[Tile["7m"]] = MR.images.tile_7m
    this[Tile["8m"]] = MR.images.tile_8m
    this[Tile["9m"]] = MR.images.tile_9m

    this[Tile["1p"]] = MR.images.tile_1p
    this[Tile["2p"]] = MR.images.tile_2p
    this[Tile["3p"]] = MR.images.tile_3p
    this[Tile["4p"]] = MR.images.tile_4p
    this[Tile["5p"]] = MR.images.tile_5p
    this[Tile["6p"]] = MR.images.tile_6p
    this[Tile["7p"]] = MR.images.tile_7p
    this[Tile["8p"]] = MR.images.tile_8p
    this[Tile["9p"]] = MR.images.tile_9p

    this[Tile["1s"]] = MR.images.tile_1s
    this[Tile["2s"]] = MR.images.tile_2s
    this[Tile["3s"]] = MR.images.tile_3s
    this[Tile["4s"]] = MR.images.tile_4s
    this[Tile["5s"]] = MR.images.tile_5s
    this[Tile["6s"]] = MR.images.tile_6s
    this[Tile["7s"]] = MR.images.tile_7s
    this[Tile["8s"]] = MR.images.tile_8s
    this[Tile["9s"]] = MR.images.tile_9s

    this[Tile["1z"]] = MR.images.tile_1z
    this[Tile["2z"]] = MR.images.tile_2z
    this[Tile["3z"]] = MR.images.tile_3z
    this[Tile["4z"]] = MR.images.tile_4z
    this[Tile["5z"]] = MR.images.tile_5z
    this[Tile["6z"]] = MR.images.tile_6z
    this[Tile["7z"]] = MR.images.tile_7z
}

val Tile.imageResource: ImageResource
    get() = tileToImgResMapping[this] ?: MR.images.tile_back

val Tile.painterResource: Painter
    @Composable
    get() = painterResource(imageResource)
