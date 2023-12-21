package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HoraResultContent(args: HoraArgs, hora: Hora) {
    with(Spacing.current) {
        LazyColumn(Modifier.fillMaxWidth()) {
            item {
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
                item {
                    VerticalSpacerBetweenPanels()

                    TopCardPanel({ Text("手牌拆解") }) {
                        Row {
                            Panel({ Text("雀头") }, Modifier.weight(3f)) {
                                Tiles(listOf(pattern.jyantou, pattern.jyantou))
                            }

                            Panel({ Text("面子") }, Modifier.weight(12f)) {
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

                        if (hora.pattern.furo.isNotEmpty()) {
                            VerticalSpacerBetweenPanels()
                            Panel({ Text("副露") }) {
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
        }
    }
}