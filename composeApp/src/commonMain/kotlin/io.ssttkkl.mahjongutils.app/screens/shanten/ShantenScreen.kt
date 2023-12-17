package io.ssttkkl.mahjongutils.app.screens.shanten

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
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.ratio.RatioGroups
import io.ssttkkl.mahjongutils.app.components.ratio.RatioOption
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.models.Tile

@Composable
fun ShantenModeRatioGroups(
    value: ShantenMode,
    onValueChanged: (ShantenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val radioOptions = listOf(
        RatioOption(
            ShantenMode.Union,
            Res.string.label_union_shanten,
            Res.string.text_union_shanten_desc
        ),
        RatioOption(
            ShantenMode.Regular,
            Res.string.label_regular_shanten,
            Res.string.text_regular_shanten_desc
        ),
    )

    RatioGroups(radioOptions, value, onValueChanged, modifier)
}

@Composable
fun ShantenScreen(onSubmit: (ShantenArgs) -> Unit) {
    var tiles by rememberSaveable { mutableStateOf(emptyList<Tile>()) }
    var shantenMode by rememberSaveable { mutableStateOf(ShantenMode.Union) }

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

            TopPanel(
                Res.string.label_shanten_mode,
                noPaddingContent = true
            ) {
                ShantenModeRatioGroups(
                    shantenMode,
                    { shantenMode = it }
                )
            }

            VerticalSpacerBetweenPanels()

            Button(
                modifier = Modifier.windowHorizontalMargin(),
                content = { Text(Res.string.text_calc) },
                onClick = {
                    onSubmit(ShantenArgs(tiles, shantenMode))
                }
            )

            VerticalSpacerBetweenPanels()
        }
    }
}