package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.models.Tile

private val contentModifier = Modifier.fillMaxWidth().padding(Spacing.medium, 0.dp)

@Composable
fun FuroShantenScreen(onSubmit: (FuroChanceShantenArgs) -> Unit) {
    var tiles by rememberSaveable { mutableStateOf(emptyList<Tile>()) }
    var chanceTile by rememberSaveable { mutableStateOf<Tile?>(null) }
    var allowChi by rememberSaveable { mutableStateOf<Boolean>(true) }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(Spacing.large))

        Panel(Res.string.label_tiles_in_hand) {
            TileField(
                value = tiles,
                onValueChange = { tiles = it },
                modifier = contentModifier
            )
        }

        Panel(Res.string.label_tile_discarded_by_other) {
            TileField(
                value = chanceTile?.let { listOf(it) } ?: emptyList(),
                onValueChange = { chanceTile = it.firstOrNull() },
                modifier = contentModifier
            )
        }

        Panel(Res.string.label_other_options) {
            Row(modifier = contentModifier, verticalAlignment = Alignment.CenterVertically) {
                Switch(allowChi, { allowChi = it })
                Text(Res.string.label_allow_chi)
            }
        }

        Button(
            modifier = Modifier.padding(Spacing.medium, 0.dp),
            content = { Text(Res.string.text_calc) },
            onClick = {
                chanceTile?.let { chanceTile ->
                    onSubmit(FuroChanceShantenArgs(tiles, chanceTile, allowChi))
                }
            }
        )

        Spacer(Modifier.height(Spacing.large))
    }
}
