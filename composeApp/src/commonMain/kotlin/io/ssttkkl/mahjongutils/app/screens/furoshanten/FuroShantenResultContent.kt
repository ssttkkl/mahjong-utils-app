package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenActionGroupsContent
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.TilesPanelHeader
import io.ssttkkl.mahjongutils.app.utils.LocalTileTextSize
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import mahjongutils.shanten.ShantenWithFuroChance

@Composable
fun FuroShantenResultContent(args: FuroChanceShantenArgs, shanten: ShantenWithFuroChance,
                             requestChangeArgs: (FuroChanceShantenArgs) -> Unit) {
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
                    FuroShantenTilesPanel(args, requestChangeArgs)
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
private fun FuroShantenTilesPanel(
    args: FuroChanceShantenArgs,
    requestChangeArgs: (FuroChanceShantenArgs) -> Unit
) {
    val editingState = rememberSaveable { mutableStateOf(false) }

    val form = remember { FuroShantenFormState() }
    LaunchedEffect(args) {
        form.fillFormWithArgs(args)
    }

    val components = remember(form) { FuroShantenComponents(form) }

    TopCardPanel({
        TilesPanelHeader(
            editingState,
            onCancel = {
                editingState.value = false
                form.fillFormWithArgs(args)
            },
            onSubmit = {
                val newArgs = form.onCheck()
                if (newArgs != null) {
                    editingState.value = false

                    if (newArgs != args) {
                        requestChangeArgs(newArgs)
                    }
                }
            }
        )
    }) {
        if (editingState.value) {
            components.Tiles()
            Spacer(Modifier.height(8.dp))
            components.ChanceTile()
        }else {
            CompositionLocalProvider(LocalTileTextSize provides TileTextSize.Default.bodyLarge) {
                FuroShantenTiles(args.tiles, args.chanceTile)
            }
        }
    }
}