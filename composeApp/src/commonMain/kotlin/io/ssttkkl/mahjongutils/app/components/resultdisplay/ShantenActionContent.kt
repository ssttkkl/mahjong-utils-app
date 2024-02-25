package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.tile.TileInlineText
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import io.ssttkkl.mahjongutils.app.utils.emoji
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_advance_tiles
import mahjongutils.composeapp.generated.resources.label_good_shape_advance_tiles
import mahjongutils.composeapp.generated.resources.label_good_shape_improvement_tiles
import mahjongutils.composeapp.generated.resources.label_waiting_tiles
import mahjongutils.composeapp.generated.resources.text_improvement_desc
import mahjongutils.composeapp.generated.resources.text_shanten_action_ankan
import mahjongutils.composeapp.generated.resources.text_shanten_action_chi_and_discard
import mahjongutils.composeapp.generated.resources.text_shanten_action_discard
import mahjongutils.composeapp.generated.resources.text_shanten_action_minkan
import mahjongutils.composeapp.generated.resources.text_shanten_action_pass
import mahjongutils.composeapp.generated.resources.text_shanten_action_pon_and_discard
import mahjongutils.composeapp.generated.resources.text_tiles_num
import mahjongutils.models.Tatsu
import mahjongutils.models.Tile
import mahjongutils.shanten.Improvement
import mahjongutils.shanten.ShantenWithoutGot
import org.jetbrains.compose.resources.stringResource

sealed class ShantenAction {
    abstract val shantenAfterAction: ShantenWithoutGot

    data class Discard(
        val tile: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction() {
        override fun toString(): String {
            return "Discard $tile"
        }
    }

    data class Ankan(
        val tile: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction() {
        override fun toString(): String {
            return "Ankan $tile"
        }
    }

    data class Chi(
        val tatsu: Tatsu,
        val discard: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction() {
        override fun toString(): String {
            return "$tatsu Chi, Discard $discard"
        }
    }

    data class Pon(
        val tile: Tile,
        val discard: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction() {
        override fun toString(): String {
            return "$tile Pon, Discard $discard"
        }
    }

    data class Minkan(
        val tile: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction() {
        override fun toString(): String {
            return "$tile Minkan"
        }
    }

    data class Pass(
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction() {
        override fun toString(): String {
            return "Pass"
        }
    }
}

@Composable
fun ShantenActionCardContent(
    action: ShantenAction,
    modifier: Modifier = Modifier
) {
    val shanten = action.shantenAfterAction
    with(Spacing.current) {
        Row(modifier) {
            ShantenActionContent(
                action,
                Modifier.width(150.dp)
            )

            VerticalDivider()

            Column(Modifier.weight(1f)) {
                TilesWithNumPanel(
                    stringResource(
                        if (shanten.shantenNum == 0)
                            Res.string.label_waiting_tiles
                        else
                            Res.string.label_advance_tiles
                    ),
                    shanten.advance.sorted(),
                    shanten.advanceNum
                )

                shanten.goodShapeAdvance?.let { goodShapeAdvance ->
                    VerticalSpacerBetweenPanels()

                    TilesWithNumPanel(
                        stringResource(Res.string.label_good_shape_advance_tiles),
                        goodShapeAdvance.sorted(),
                        shanten.goodShapeAdvanceNum ?: 0,
                        1.0 * (shanten.goodShapeAdvanceNum ?: 0) / shanten.advanceNum
                    )
                }

                shanten.goodShapeImprovement?.let { goodShapeImprovement ->
                    VerticalSpacerBetweenPanels()

                    ImprovementsPanel(
                        stringResource(Res.string.label_good_shape_improvement_tiles),
                        goodShapeImprovement,
                        shanten.goodShapeImprovementNum ?: 0
                    )
                }
            }
        }
    }
}

@Composable
private fun ShantenActionContent(
    action: ShantenAction,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    Row(modifier, horizontalArrangement, verticalAlignment) {
        CompositionLocalProvider(LocalTileTextSize provides TileTextSize.Default.bodyLarge) {
            when (action) {
                is ShantenAction.Discard -> {
                    TileInlineText(
                        stringResource(
                            Res.string.text_shanten_action_discard,
                            action.tile.emoji
                        )
                    )
                }

                is ShantenAction.Ankan -> {
                    TileInlineText(
                        stringResource(
                            Res.string.text_shanten_action_ankan,
                            action.tile.emoji
                        )
                    )
                }

                is ShantenAction.Chi -> {
                    TileInlineText(
                        stringResource(
                            Res.string.text_shanten_action_chi_and_discard,
                            action.tatsu.first.emoji,
                            action.tatsu.second.emoji,
                            action.discard.emoji
                        )
                    )
                }

                is ShantenAction.Pon -> {
                    TileInlineText(
                        stringResource(
                            Res.string.text_shanten_action_pon_and_discard,
                            action.discard.emoji
                        )
                    )
                }

                is ShantenAction.Minkan -> {
                    TileInlineText(
                        stringResource(
                            Res.string.text_shanten_action_minkan,
                            action.tile.emoji
                        )
                    )
                }

                is ShantenAction.Pass -> {
                    TileInlineText(stringResource(Res.string.text_shanten_action_pass))
                }
            }
        }
    }
}

@Composable
private fun ImprovementsPanel(
    label: String,
    improvement: Map<Tile, List<Improvement>>,
    improvementNum: Int
) {
    Panel({ Text(label) }) {
        improvement.map {
            // tile, discard_tiles, advance_num
            // 摸tile, 打discard_tiles, 进advance_num张
            Triple(
                it.key,
                it.value.map { it.discard },
                it.value.firstOrNull()?.advanceNum ?: 0
            )
        }.forEach { (tile, discardTiles, advanceNum) ->
            TileInlineText(
                stringResource(
                    Res.string.text_improvement_desc,
                    tile.emoji,
                    discardTiles.joinToString("") { it.emoji },
                    advanceNum
                )
            )
            Spacer(Modifier.height(8.dp))
        }
        Text(
            stringResource(Res.string.text_tiles_num, improvementNum),
            style = MaterialTheme.typography.labelMedium
        )
    }
}