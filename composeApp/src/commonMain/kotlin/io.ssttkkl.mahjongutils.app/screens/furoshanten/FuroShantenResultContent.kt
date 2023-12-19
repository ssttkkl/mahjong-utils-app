package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenActionCardContent
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.TilesTopCardPanel
import io.ssttkkl.mahjongutils.app.components.tiles.Tiles
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.shantenNumText
import mahjongutils.shanten.ShantenWithFuroChance

@Composable
fun FuroShantenResultContent(args: FuroChanceShantenArgs, shanten: ShantenWithFuroChance) {
    val scrollState = rememberScrollState()

    with(Spacing.current) {
        Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            VerticalSpacerBetweenPanels()

            TilesTopCardPanel(
                stringResource(MR.strings.label_tiles_in_hand),
                args.tiles,
                tracingElement = {
                    Text(stringResource(MR.strings.label_tile_discarded_by_other_short))
                    Tiles(listOf(args.chanceTile), Modifier.height(36.dp))
                }
            )

            VerticalSpacerBetweenPanels()

            ShantenNumCardPanel(shanten.shantenNum)

            VerticalSpacerBetweenPanels()

            // shanten to actions (asc sorted)
            val groups: List<Pair<Int, List<ShantenAction>>> = remember(shanten) {
                val groupedShanten = mutableMapOf<Int, MutableList<ShantenAction>>()
                fun getGroup(shantenNum: Int) =
                    groupedShanten.getOrPut(shantenNum) { mutableListOf() }

                shanten.chi.forEach { (tatsu, shantenAfterChi) ->
                    shantenAfterChi.discardToAdvance.forEach { (discard, shantenAfterAction) ->
                        getGroup(shantenAfterAction.shantenNum).add(
                            ShantenAction.Chi(
                                tatsu,
                                discard,
                                shantenAfterAction
                            )
                        )
                    }
                }

                shanten.pon?.let { shantenAfterPon ->
                    shantenAfterPon.discardToAdvance.forEach { (discard, shantenAfterAction) ->
                        getGroup(shantenAfterAction.shantenNum).add(
                            ShantenAction.Pon(
                                args.chanceTile,
                                discard,
                                shantenAfterAction
                            )
                        )
                    }
                }

                shanten.minkan?.let { shantenAfterAction ->
                    getGroup(shantenAfterAction.shantenNum).add(
                        ShantenAction.Minkan(
                            args.chanceTile,
                            shantenAfterAction
                        )
                    )
                }

                shanten.pass?.let { shantenAfterAction ->
                    getGroup(shantenAfterAction.shantenNum).add(
                        ShantenAction.Pass(shantenAfterAction)
                    )
                }

                groupedShanten.toList().sortedBy { it.first }
            }

            groups.forEach { (shantenNum, actions) ->
                val label = stringResource(
                    if (shantenNum == shanten.shantenNum)
                        MR.strings.label_shanten_action
                    else
                        MR.strings.label_shanten_action_backwards,
                    shantenNumText(shantenNum)
                )

                val content: Array<@Composable BoxScope.() -> Unit> = Array(actions.size) {
                    @Composable { ShantenActionCardContent(actions[it]) }
                }
                TopCardPanel(label, content = content)

                VerticalSpacerBetweenPanels()
            }
        }
    }
}
