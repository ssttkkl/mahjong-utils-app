package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.components.tile.FuroTiles
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.screens.hanhu.PointPanel
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import io.ssttkkl.mahjongutils.app.utils.localizedName
import mahjongutils.hora.Hora
import mahjongutils.hora.RegularHoraHandPattern
import mahjongutils.models.Wind

@Composable
private fun HandTilesPanel(args: HoraArgs) {
    TopCardPanel({ Text(stringResource(MR.strings.label_tiles_in_hand)) }) {
        CompositionLocalProvider(LocalTileTextSize provides TileTextSize.Default.bodyLarge) {
            HoraTiles(args)
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HandDeconstructionPanel(pattern: RegularHoraHandPattern) {
    with(Spacing.current) {
        TopCardPanel({ Text(stringResource(MR.strings.label_hand_deconstruction)) }) {
            Row {
                Panel({ Text(stringResource(MR.strings.label_jyantou)) }, Modifier.weight(4f)) {
                    Tiles(listOf(pattern.jyantou, pattern.jyantou))
                }

                Spacer(Modifier.width(8.dp))

                Panel({ Text(stringResource(MR.strings.label_mentsu)) }, Modifier.weight(12f)) {
                    FlowRow {
                        pattern.menzenMentsu.sortedBy { it.tiles.first() }
                            .forEachIndexed { index, mentsu ->
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

                            FuroTiles(furo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YakuPanel(hora: Hora) {
    TopCardPanel({ Text(stringResource(MR.strings.label_yaku)) }) {
        hora.yaku.sortedBy { it.han }.forEachIndexed { index, yaku ->
            if (index != 0) {
                Spacer(Modifier.height(8.dp))
            }

            Text(stringResource(yaku.localizedName))
        }

        if (hora.yaku.isNotEmpty() && hora.dora > 0) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(MR.strings.label_yaku_dora, hora.dora))
        }
    }
}

@Composable
fun HoraResultContent(args: HoraArgs, hora: Hora) {
    val state = rememberLazyListState()

    with(Spacing.current) {
        VerticalScrollBox(state) {
            LazyColumn(Modifier.fillMaxWidth(), state = state) {
                item("hand") {
                    VerticalSpacerBetweenPanels()

                    HandTilesPanel(args)
                }

                item("point") {
                    VerticalSpacerBetweenPanels()
                    PointPanel(
                        hora.han, hora.hu, hora.hasYakuman,
                        if (hora.selfWind == null || hora.selfWind == Wind.East) hora.parentPoint else null,
                        if (hora.selfWind == null || hora.selfWind != Wind.East) hora.childPoint else null
                    )
                }

                val pattern = hora.pattern
                if (pattern is RegularHoraHandPattern) {
                    item("pattern") {
                        VerticalSpacerBetweenPanels()
                        HandDeconstructionPanel(pattern)
                    }
                }

                item("yaku") {
                    VerticalSpacerBetweenPanels()
                    YakuPanel(hora)
                }

                item("bottom_spacer") {
                    VerticalSpacerBetweenPanels()
                }
            }
        }
    }
}