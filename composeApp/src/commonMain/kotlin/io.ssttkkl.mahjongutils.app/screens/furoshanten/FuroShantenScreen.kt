package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.switch.SwitchItem
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.models.Tile

@Composable
fun FuroShantenScreen(onSubmit: (FuroChanceShantenArgs) -> Unit) {
    var tiles by rememberSaveable { mutableStateOf(emptyList<Tile>()) }
    var chanceTile by rememberSaveable { mutableStateOf<Tile?>(null) }
    var allowChi by rememberSaveable { mutableStateOf<Boolean>(true) }

    with(Spacing.current) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            VerticalSpacerBetweenPanels()

            TopPanel(Res.string.label_tiles_in_hand) {
                TileField(
                    value = tiles,
                    onValueChange = { tiles = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            VerticalSpacerBetweenPanels()

            TopPanel(Res.string.label_tile_discarded_by_other) {
                TileField(
                    value = chanceTile?.let { listOf(it) } ?: emptyList(),
                    onValueChange = { chanceTile = it.firstOrNull() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            VerticalSpacerBetweenPanels()

            TopPanel(
                Res.string.label_other_options,
                noPaddingContent = true
            ) {
                SwitchItem(allowChi, { allowChi = !allowChi }, Res.string.label_allow_chi)
            }

            VerticalSpacerBetweenPanels()

            Button(
                modifier = Modifier.windowHorizontalMargin(),
                content = { Text(Res.string.text_calc) },
                onClick = {
                    chanceTile?.let { chanceTile ->
                        onSubmit(FuroChanceShantenArgs(tiles, chanceTile, allowChi))
                    }
                }
            )

            VerticalSpacerBetweenPanels()
        }
    }
}