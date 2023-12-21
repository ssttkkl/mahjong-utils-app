package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import mahjongutils.hora.Hora
import mahjongutils.hora.RegularHoraHandPattern
import mahjongutils.models.Wind

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HandDeconstructionPanel(pattern: RegularHoraHandPattern) {
    with(Spacing.current) {
        TopCardPanel({ Text(stringResource(MR.strings.label_hand_deconstruction)) }) {
            Row {
                Panel({ Text(stringResource(MR.strings.label_jyantou)) }, Modifier.weight(3f)) {
                    Tiles(listOf(pattern.jyantou, pattern.jyantou))
                }

                Panel({ Text(stringResource(MR.strings.label_mentsu)) }, Modifier.weight(12f)) {
                    FlowRow {
                        pattern.menzenMentsu.forEachIndexed { index, mentsu ->
                            if (index != 0) {
                                Spacer(Modifier.width(8.dp))
                            }

                            Tiles(mentsu.tiles)
                        }
                    }
                }
            }

            if (pattern.furo.isNotEmpty()) {
                VerticalSpacerBetweenPanels()
                Panel({ Text(stringResource(MR.strings.label_furo)) }) {
                    FlowRow {
                        pattern.furo.forEachIndexed { index, furo ->
                            if (index != 0) {
                                Spacer(Modifier.width(8.dp))
                            }

                            Tiles(furo.tiles)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HoraInfoRow(title: String, content: @Composable RowScope.() -> Unit) {
    Row {
        Text(
            title, Modifier.width(100.dp),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        content()
    }
}

@Composable
private fun HoraInfoPanel(hora: Hora) {
    with(Spacing.current) {
        TopCardPanel({ Text("Hora Info") }) {
            HoraInfoRow("役种") {
                Text(hora.yaku.joinToString())
            }
            Divider(Modifier.padding(vertical = panelsVerticalSpacing / 2))
            HoraInfoRow("番数") {
                Text(hora.han.toString())
            }
            Divider(Modifier.padding(vertical = panelsVerticalSpacing / 2))
            HoraInfoRow("符数") {
                Text(hora.hu.toString())
            }
            Divider(Modifier.padding(vertical = panelsVerticalSpacing / 2))
            HoraInfoRow("点数") {
                if (hora.selfWind == Wind.East) {
                    Text(hora.parentPoint.toString())
                } else {
                    Text(hora.childPoint.toString())
                }
            }
        }
    }
}

@Composable
fun HoraResultContent(args: HoraArgs, hora: Hora) {
    with(Spacing.current) {
        LazyColumn(Modifier.fillMaxWidth()) {
            item("hand") {
                VerticalSpacerBetweenPanels()

                TopCardPanel({ Text(stringResource(MR.strings.label_tiles_in_hand)) }) {
                    CompositionLocalProvider(LocalTileTextSize provides TileTextSize.Default.bodyLarge) {
                        Row {
                            Tiles(args.tiles.dropLast(1))
                            Tiles(
                                listOf(args.tiles.last()),
                                Modifier.rotate(-90f)
                            )
                        }
                    }
                }
            }

            val pattern = hora.pattern
            if (pattern is RegularHoraHandPattern) {
                item("pattern") {
                    VerticalSpacerBetweenPanels()
                    HandDeconstructionPanel(pattern)
                }
            }

            item("info") {
                VerticalSpacerBetweenPanels()
                HoraInfoPanel(hora)
            }
        }
    }
}