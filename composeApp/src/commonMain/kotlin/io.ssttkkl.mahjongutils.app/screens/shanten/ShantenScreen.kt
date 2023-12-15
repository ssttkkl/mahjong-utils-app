package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType.Companion.Sp
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.ratiogroups.RatioGroups
import io.ssttkkl.mahjongutils.app.components.ratiogroups.RatioOption
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.models.Tile

@Composable
fun ShantenModeRatioGroups(
    value: ShantenMode,
    onValueChanged: (ShantenMode) -> Unit
) {
    val radioOptions = listOf(
        RatioOption(
            ShantenMode.Union,
            Res.string.label_union_shanten,
            Res.string.label_union_shanten_desc
        ),
        RatioOption(
            ShantenMode.Regular,
            Res.string.label_regular_shanten,
            Res.string.label_regular_shanten_desc
        ),
    )

    RatioGroups(radioOptions, value, onValueChanged)
}

@Composable
fun ShantenScreen(onSubmit: (ShantenArgs) -> Unit) {
    var tiles by remember { mutableStateOf("") }
    var shantenMode by remember { mutableStateOf(ShantenMode.Union) }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.fillMaxWidth().height(Spacing.large))

        Panel(Res.string.label_tiles_input) {
            TextField(
                value = tiles,
                onValueChange = { tiles = it },
                modifier = Modifier.fillMaxWidth().padding(Spacing.medium, 0.dp),
                textStyle = TextStyle.Default.copy(fontSize = TextUnit(36f, Sp)),
            )
        }

        Spacer(Modifier.fillMaxWidth().height(Spacing.medium))

        Panel(Res.string.label_shanten_mode) {
            ShantenModeRatioGroups(shantenMode) { shantenMode = it }
        }

        Spacer(Modifier.fillMaxWidth().height(Spacing.medium))

        Button(
            modifier = Modifier.padding(Spacing.medium, 0.dp),
            content = { Text("Submit") },
            onClick = {
                onSubmit(ShantenArgs(Tile.parseTiles(tiles), shantenMode))
            }
        )

        Spacer(Modifier.fillMaxWidth().height(Spacing.large))
    }
}
