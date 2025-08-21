package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.tile.FuroTiles
import io.ssttkkl.mahjongutils.app.components.tile.LieDownTileImage
import io.ssttkkl.mahjongutils.app.components.tile.TileInlineAutoSingleLineText
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.components.tile.annotatedAsInline
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.base.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.base.utils.removeLast

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HoraTiles(args: HoraArgs) {
    val tilesExcludingAgari = remember(args.tiles, args.agari) { args.tiles.removeLast(args.agari) }
    FlowRow {
        Row(Modifier.padding(end = 8.dp)) {
            val preferTileSize = LocalTileTextSize.current
            var reducedTileSize by remember { mutableStateOf(preferTileSize) }

            TileInlineAutoSingleLineText(
                buildAnnotatedString {
                    append(tilesExcludingAgari.annotatedAsInline())
                },
                fontSize = preferTileSize,
                onTextSizeConstrained = {
                    reducedTileSize = it.textSize
                }
            )
            Tiles(
                listOf(args.agari),
                modifier = Modifier.align(Alignment.Bottom),
                fontSize = reducedTileSize,
                tileImage = { LieDownTileImage(it) }
            )
        }
        if (args.furo.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            args.furo.forEach {
                Spacer(Modifier.width(8.dp))
                FuroTiles(it)
            }
        }
    }
}