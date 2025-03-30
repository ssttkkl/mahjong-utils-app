package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.capturable.LazyCapturableColumn
import io.ssttkkl.mahjongutils.app.components.onEnterKeyDown
import io.ssttkkl.mahjongutils.app.base.components.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenActionGroupsContent
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.TilesWithNumTopCardPanel
import io.ssttkkl.mahjongutils.app.base.components.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.components.tile.AutoSingleLineTiles
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState
import io.ssttkkl.mahjongutils.app.screens.common.TilesPanelHeader
import io.ssttkkl.mahjongutils.app.base.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_advance_tiles
import mahjongutils.composeapp.generated.resources.label_good_shape_advance_tiles
import mahjongutils.composeapp.generated.resources.text_tiles_with_got
import mahjongutils.composeapp.generated.resources.text_tiles_without_got
import mahjongutils.shanten.CommonShanten
import mahjongutils.shanten.ShantenWithGot
import mahjongutils.shanten.ShantenWithoutGot
import mahjongutils.shanten.asWithGot
import mahjongutils.shanten.asWithoutGot
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShantenResultContent(
    args: ShantenArgs,
    shanten: CommonShanten,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
    if (shanten is ShantenWithoutGot) {
        ShantenWithoutGotResultContent(
            args,
            shanten.asWithoutGot,
            requestChangeArgs
        )
    } else {
        ShantenWithGotResultContent(
            args,
            shanten.asWithGot,
            requestChangeArgs
        )
    }
}

@Composable
private fun ShantenWithoutGotResultContent(
    args: ShantenArgs, shanten: ShantenWithoutGot,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
    val panelState = remember { EditablePanelState(args, ShantenFormState()) }
    LaunchedEffect(args) {
        panelState.originArgs = args
    }

    val lazyListState = rememberLazyListState()

    with(Spacing.current) {
        VerticalScrollBox(lazyListState) {
            LazyCapturableColumn(
                LocalAppState.current.captureController,
                Modifier.fillMaxWidth(),
                state = lazyListState
            ) {
                item("hand") {
                    VerticalSpacerBetweenPanels()
                    TilesInHandPanel(args, false, panelState, requestChangeArgs)
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
}

@Composable
private fun ShantenWithGotResultContent(
    args: ShantenArgs,
    shanten: ShantenWithGot,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
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

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val panelState = remember { EditablePanelState(args, ShantenFormState()) }
    LaunchedEffect(args) {
        panelState.originArgs = args
    }
    val fillbackHandler = remember {
        ShantenFillbackHandler(panelState) {
            scope.launch {
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    with(Spacing.current) {
        VerticalScrollBox(lazyListState) {
            LazyCapturableColumn(
                LocalAppState.current.captureController,
                Modifier.fillMaxWidth(),
                state = lazyListState
            ) {
                item {
                    VerticalSpacerBetweenPanels()
                    TilesInHandPanel(args, true, panelState, requestChangeArgs)
                }

                item {
                    VerticalSpacerBetweenPanels()
                    ShantenNumCardPanel(shanten.shantenNum)
                }

                item {
                    VerticalSpacerBetweenPanels()
                }

                ShantenActionGroupsContent(groups, shanten.shantenNum, fillbackHandler)
            }
        }
    }
}

@Composable
private fun TilesInHandPanel(
    args: ShantenArgs,
    withGot: Boolean,
    state: EditablePanelState<ShantenFormState, ShantenArgs>,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
    val components = remember(state.form) { ShantenFormComponents(state.form) }

    val onSubmit = {
        val newArgs = state.form.onCheck()
        if (newArgs != null) {
            state.editing = false

            if (newArgs != args) {
                requestChangeArgs(newArgs)
            }
        }
    }
    val onCancel = {
        state.editing = false
        state.form.fillFormWithArgs(args)
    }

    TopCardPanel(
        header = {
            TilesPanelHeader(panelState = state, onCancel = onCancel, onSubmit = onSubmit)
        },
        caption = {
            if (!state.editing) {
                Text(
                    stringResource(
                        if (withGot)
                            Res.string.text_tiles_with_got
                        else
                            Res.string.text_tiles_without_got
                    )
                )
            }
        }
    ) {
        if (state.editing) {
            components.Tiles(Modifier.onEnterKeyDown(onSubmit), true)
        } else {
            AutoSingleLineTiles(args.tiles, fontSize = TileTextSize.Default.bodyLarge)
        }
    }
}
