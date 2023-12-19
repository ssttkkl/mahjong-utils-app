package io.ssttkkl.mahjongutils.app.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.tiles.Tile
import io.ssttkkl.mahjongutils.app.components.tiles.Tiles
import io.ssttkkl.mahjongutils.app.utils.percentile


enum class ShantenActionTableType {
    Normal, WithGoodShapeAdvance, WithGoodShapeImprovement
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun columns(): List<TableColumn<ShantenAction>> = listOf(
    TableColumn("", 3f) { record, index ->
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            when (record) {
                is ShantenAction.Discard -> {
                    Text(stringResource(MR.strings.text_shanten_action_discard))
                    Tile(record.tile)
                }

                is ShantenAction.Ankan -> {
                    Text(stringResource(MR.strings.text_shanten_action_ankan))
                    Tile(record.tile)
                }

                is ShantenAction.Chi -> {
                    Tiles(listOf(record.tatsu.first, record.tatsu.second))
                    Text(
                        stringResource(MR.strings.text_shanten_action_chi)
                                + stringResource(MR.strings.text_shanten_action_and)
                                + stringResource(MR.strings.text_shanten_action_discard)
                    )
                    Tile(record.discard)
                }

                is ShantenAction.Pon -> {
                    Text(
                        stringResource(MR.strings.text_shanten_action_pon)
                                + stringResource(MR.strings.text_shanten_action_and)
                                + stringResource(MR.strings.text_shanten_action_discard)
                    )
                    Tile(record.discard)
                }

                is ShantenAction.Minkan -> {
                    Text(stringResource(MR.strings.text_shanten_action_minkan))
                    Tile(record.tile)
                }

                is ShantenAction.Pass -> {
                    Text(stringResource(MR.strings.text_shanten_action_pass))
                }
            }
        }
    },
    TableColumn(stringResource(MR.strings.label_advance_tiles), 9f) { record, index ->
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Tiles(
                record.shantenAfterAction.advance.sorted(),
                modifier = Modifier.padding(2.dp),
                tileModifier = Modifier.padding(2.dp).height(30.dp)
            )
            Text(
                stringResource(MR.strings.text_tiles_num, record.shantenAfterAction.advanceNum),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
)

@Composable
private fun columnsWithGoodShapeAdvance(): List<TableColumn<ShantenAction>> {
    val columns = columns()
    return listOf(
        columns[0],
        columns[1].copy(weight = 5f),
        TableColumn(
            stringResource(MR.strings.label_good_shape_advance_tiles),
            4f
        ) { record, index ->
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                record.shantenAfterAction.goodShapeAdvance?.let { goodShapeAdvance ->
                    record.shantenAfterAction.goodShapeAdvanceNum?.let { goodShapeAdvanceNum ->
                        Tiles(
                            goodShapeAdvance.sorted(),
                            modifier = Modifier.padding(2.dp),
                            tileModifier = Modifier.padding(2.dp).height(30.dp)
                        )
                        Text(
                            stringResource(
                                MR.strings.text_good_shape_advance_tiles_num,
                                goodShapeAdvanceNum,
                                (goodShapeAdvanceNum.toDouble() / record.shantenAfterAction.advanceNum).percentile()
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun columnsWithGoodShapeImprovement(): List<TableColumn<ShantenAction>> {
    val columns = columns()
    return listOf(
        columns[0],
        columns[1].copy(weight = 5f),
        TableColumn(
            stringResource(MR.strings.label_good_shape_improvement_tiles),
            4f
        ) { record, index ->
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                record.shantenAfterAction.goodShapeImprovement?.let { goodShapeImprovement ->
                    record.shantenAfterAction.goodShapeImprovementNum?.let { goodShapeImprovementNum ->
                        goodShapeImprovement.map {
                            // tile, discard_tiles, advance_num
                            // 摸tile, 打discard_tiles, 进advance_num张
                            Triple(
                                it.key,
                                it.value.map { it.discard },
                                it.value.firstOrNull()?.advanceNum ?: 0
                            )
                        }.forEach { (tile, discardTiles, advanceNum) ->
                            FlowRow {
                                Tiles(
                                    listOf(tile),
                                    modifier = Modifier.padding(2.dp),
                                    tileModifier = Modifier.padding(2.dp).height(30.dp)
                                )
                                Text(stringResource(MR.strings.text_shanten_action_discard))
                                Tiles(
                                    discardTiles, modifier = Modifier.padding(2.dp),
                                    tileModifier = Modifier.padding(2.dp).height(24.dp)
                                )
                                Text(stringResource(MR.strings.text_waiting_tiles_num, advanceNum))
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(
                            stringResource(MR.strings.text_tiles_num, goodShapeImprovementNum),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ShantenActionTable(
    actions: List<ShantenAction>,
    type: ShantenActionTableType,
    modifier: Modifier = Modifier,
) {
    val col = when (type) {
        ShantenActionTableType.Normal -> columns()
        ShantenActionTableType.WithGoodShapeAdvance -> columnsWithGoodShapeAdvance()
        ShantenActionTableType.WithGoodShapeImprovement -> columnsWithGoodShapeImprovement()
    }
    Table(col, actions, modifier)
}