package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenActionGroupsContent
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import mahjongutils.models.Tile
import mahjongutils.shanten.ShantenWithFuroChance

@Composable
private fun FuroShantenTilesPanel(tiles: List<Tile>, chanceTile: Tile) {
    TopCardPanel({ Text(stringResource(MR.strings.label_tiles_in_hand)) }) {
        CompositionLocalProvider(LocalTileTextSize provides TileTextSize.Default.bodyLarge) {
            FuroShantenTiles(tiles, chanceTile)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuroShantenResultContent(args: FuroChanceShantenArgs, shanten: ShantenWithFuroChance) {
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
                    FuroShantenTilesPanel(args.tiles, args.chanceTile)
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
