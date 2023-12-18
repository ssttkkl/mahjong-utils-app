package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.tiles.Tiles
import io.ssttkkl.mahjongutils.app.utils.format
import mahjongutils.models.Tile

@Composable
fun TilesPanel(
    label: String,
    tiles: List<Tile>,
    tracingElement: (@Composable RowScope.() -> Unit)? = null,
    caption: String? = null,
    tileModifier: Modifier = Modifier.height(30.dp)
) {
    Panel(label) {
        Column {
            if (tracingElement != null) {
                Row {
                    Tiles(tiles, tileModifier)
                    Spacer(Modifier.width(8.dp))
                    tracingElement()
                }
            } else {
                Tiles(tiles, tileModifier)
            }

            if (caption != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    caption,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun TilesTopCardPanel(
    label: String,
    tiles: List<Tile>,
    tracingElement: (@Composable RowScope.() -> Unit)? = null,
    caption: String? = null,
    tileModifier: Modifier = Modifier.height(30.dp)
) {
    TopCardPanel(label, {
        Column {
            if (tracingElement != null) {
                Row {
                    Tiles(tiles, tileModifier)
                    Spacer(Modifier.width(8.dp))
                    tracingElement()
                }
            } else {
                Tiles(tiles, tileModifier)
            }

            if (caption != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    caption,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    })
}


@Composable
fun TilesWithNumTopCardPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int
) {
    TilesTopCardPanel(
        label,
        tiles.sorted(),
        caption = Res.string.text_tiles_num.format(tileNum)
    )
}

@Composable
fun TilesWithNumPanel(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int,
    tileModifier: Modifier = Modifier.height(30.dp)
) {
    TilesPanel(
        label,
        tiles.sorted(),
        caption = Res.string.text_tiles_num.format(tileNum),
        tileModifier = tileModifier
    )
}