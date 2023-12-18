package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenActionCardContent
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.TilesTopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.TilesWithNumTopCardPanel
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.format
import io.ssttkkl.mahjongutils.app.utils.shantenNumText
import mahjongutils.models.Tile
import mahjongutils.shanten.ShantenWithGot
import mahjongutils.shanten.ShantenWithoutGot

@Composable
fun ShantenResultContent(args: ShantenArgs, shanten: ShantenWithoutGot) {
    val scrollState = rememberScrollState()

    with(Spacing.current) {
        Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            VerticalSpacerBetweenPanels()

            TilesInHandPanel(args.tiles, false)

            VerticalSpacerBetweenPanels()

            ShantenNumCardPanel(shanten.shantenNum)

            VerticalSpacerBetweenPanels()

            TilesWithNumTopCardPanel(
                Res.string.label_advance_tiles,
                shanten.advance,
                shanten.advanceNum
            )

            VerticalSpacerBetweenPanels()

            shanten.goodShapeAdvance?.let { goodShapeAdvance ->
                shanten.goodShapeAdvanceNum?.let { goodShapeAdvanceNum ->
                    TilesWithNumTopCardPanel(
                        Res.string.label_good_shape_advance_tiles,
                        goodShapeAdvance,
                        goodShapeAdvanceNum
                    )
                }
            }
        }
    }
}

@Composable
fun ShantenResultContent(args: ShantenArgs, shanten: ShantenWithGot) {
    val scrollState = rememberScrollState()

    with(Spacing.current) {
        Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            VerticalSpacerBetweenPanels()

            TilesInHandPanel(args.tiles, true)

            VerticalSpacerBetweenPanels()

            ShantenNumCardPanel(shanten.shantenNum)

            VerticalSpacerBetweenPanels()

            // shanten to actions (asc sorted)
            val groups: List<Pair<Int, List<ShantenAction>>> = remember(shanten) {
                val groupedShanten = mutableMapOf<Int, MutableList<ShantenAction>>()
                fun getGroup(shantenNum: Int) =
                    groupedShanten.getOrPut(shantenNum) { mutableListOf() }

                shanten.discardToAdvance.forEach { (discard, shantenAfterAction) ->
                    getGroup(shantenAfterAction.shantenNum).add(
                        ShantenAction.Discard(
                            discard,
                            shantenAfterAction
                        )
                    )
                }

                shanten.ankanToAdvance.forEach { (ankan, shantenAfterAction) ->
                    getGroup(shantenAfterAction.shantenNum).add(
                        ShantenAction.Ankan(
                            ankan,
                            shantenAfterAction
                        )
                    )
                }

                groupedShanten.toList().sortedBy { it.first }
            }

            groups.forEach { (shantenNum, actions) ->
                val label_shanten_action = if (shantenNum == shanten.shantenNum)
                    Res.string.label_shanten_action
                else
                    Res.string.label_shanten_action_backwards

                val label = label_shanten_action.format(
                    shantenNumText(shantenNum)
                )

                val content: Array<@Composable () -> Unit> = Array(actions.size) {
                    @Composable { ShantenActionCardContent(actions[it]) }
                }
                TopCardPanel(label, *content)

                VerticalSpacerBetweenPanels()
            }
        }
    }

}

@Composable
private fun TilesInHandPanel(tiles: List<Tile>, withGot: Boolean) {
    TilesTopCardPanel(
        Res.string.label_tiles_in_hand,
        tiles,
        caption = if (withGot) Res.string.text_tiles_with_got else Res.string.text_tiles_without_got,
        tileModifier = Modifier.height(36.dp)
    )
}