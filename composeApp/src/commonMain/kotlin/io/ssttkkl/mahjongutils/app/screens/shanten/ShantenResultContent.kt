package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenActionGroupsContent
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.TilesWithNumTopCardPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.components.tile.AutoSingleLineTiles
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.models.Tile
import mahjongutils.shanten.ShantenWithGot
import mahjongutils.shanten.ShantenWithoutGot
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShantenResultContent(args: ShantenArgs, shanten: ShantenWithoutGot) {
    with(Spacing.current) {
        LazyColumn(Modifier.fillMaxWidth()) {
            item("hand") {
                VerticalSpacerBetweenPanels()
                TilesInHandPanel(args.tiles, false)
            }

            item("shantenNum") {
                VerticalSpacerBetweenPanels()
                ShantenNumCardPanel(shanten.shantenNum)
            }

            item("advance") {
                VerticalSpacerBetweenPanels()
                TilesWithNumTopCardPanel(
                    stringResource(Res.string.label_advance_tiles),
                    shanten.advance,
                    shanten.advanceNum
                )
            }

            item("goodShapeAdvance") {
                shanten.goodShapeAdvance?.let { goodShapeAdvance ->
                    shanten.goodShapeAdvanceNum?.let { goodShapeAdvanceNum ->
                        VerticalSpacerBetweenPanels()
                        TilesWithNumTopCardPanel(
                            stringResource(Res.string.label_good_shape_advance_tiles),
                            goodShapeAdvance,
                            goodShapeAdvanceNum,
                            1.0 * (shanten.goodShapeAdvanceNum ?: 0) / shanten.advanceNum
                        )
                    }
                }
            }

            item("footer") {
                VerticalSpacerBetweenPanels()
            }
        }
    }
}

@Composable
fun ShantenResultContent(args: ShantenArgs, shanten: ShantenWithGot) {
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

        groupedShanten.mapValues {
            // 按照进张降序排序
            it.value.sortedByDescending { it.shantenAfterAction.advanceNum }
        }
            .toList()
            .sortedBy { it.first }  // 按照向听数排序
    }

    val state = rememberLazyListState()

    with(Spacing.current) {
        VerticalScrollBox(state) {
            LazyColumn(Modifier.fillMaxWidth(), state = state) {
                item {
                    VerticalSpacerBetweenPanels()
                    TilesInHandPanel(args.tiles, true)
                }

                item {
                    VerticalSpacerBetweenPanels()
                    ShantenNumCardPanel(shanten.shantenNum)
                }

                item {
                    VerticalSpacerBetweenPanels()
                }

                ShantenActionGroupsContent(groups, shanten.shantenNum)
            }
        }
    }
}

@Composable
private fun TilesInHandPanel(tiles: List<Tile>, withGot: Boolean) {
    TopCardPanel(
        { Text(stringResource(Res.string.label_tiles_in_hand)) },
        caption = {
            Text(
                stringResource(
                    if (withGot)
                        Res.string.text_tiles_with_got
                    else
                        Res.string.text_tiles_without_got
                )
            )
        }
    ) {
        AutoSingleLineTiles(tiles, fontSize = TileTextSize.Default.bodyLarge)
    }
}