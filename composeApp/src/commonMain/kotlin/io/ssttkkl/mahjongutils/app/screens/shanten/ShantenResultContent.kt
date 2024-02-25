package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import mahjongutils.composeapp.generated.resources.label_advance_tiles
import mahjongutils.composeapp.generated.resources.label_good_shape_advance_tiles
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
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
        ShantenWithoutGotResultContent(args, shanten.asWithoutGot, requestChangeArgs)
    } else {
        ShantenWithGotResultContent(args, shanten.asWithGot, requestChangeArgs)
    }
}

@Composable
private fun ShantenWithoutGotResultContent(
    args: ShantenArgs, shanten: ShantenWithoutGot,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
    with(Spacing.current) {
        LazyColumn(Modifier.fillMaxWidth()) {
            item("hand") {
                VerticalSpacerBetweenPanels()
                TilesInHandPanel(args, false, requestChangeArgs)
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
private fun ShantenWithGotResultContent(
    args: ShantenArgs, shanten: ShantenWithGot,
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

    val state = rememberLazyListState()

    with(Spacing.current) {
        VerticalScrollBox(state) {
            LazyColumn(Modifier.fillMaxWidth(), state = state) {
                item {
                    VerticalSpacerBetweenPanels()
                    TilesInHandPanel(args, true, requestChangeArgs)
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
private fun TilesInHandPanel(
    args: ShantenArgs,
    withGot: Boolean,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
    var editing = rememberSaveable { mutableStateOf(false) }
    if (editing.value) {
        TilesInHandPanelEditing(editing, args, requestChangeArgs)
    } else {
        TilesInHandPanelShowing(editing, args, withGot)
    }
}

@Composable
private fun TilesInHandPanelShowing(
    editingState: MutableState<Boolean>,
    args: ShantenArgs,
    withGot: Boolean
) {
    TopCardPanel(
        header = {
            Row(Modifier.height(24.dp)) {
                Text(
                    stringResource(Res.string.label_tiles_in_hand),
                    Modifier.align(Alignment.CenterVertically)
                )

                IconButton(
                    { editingState.value = true },
                    Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Outlined.Edit, "", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        caption = {
            Text(
                stringResource(
                    if (withGot)
                        Res.string.text_tiles_with_got
                    else
                        Res.string.text_tiles_without_got
                )
            )
        },
        content = {
            AutoSingleLineTiles(args.tiles, fontSize = TileTextSize.Default.bodyLarge)
        },
    )
}

@Composable
private fun TilesInHandPanelEditing(
    editingState: MutableState<Boolean>,
    args: ShantenArgs,
    requestChangeArgs: (ShantenArgs) -> Unit
) {
    val form = remember { ShantenFormState() }
    LaunchedEffect(args) {
        form.fillFormWithArgs(args)
    }

    val components = remember(form) { ShantenFormComponents(form) }

    TopCardPanel(
        header = {
            Row(Modifier.height(24.dp)) {
                Text(
                    stringResource(Res.string.label_tiles_in_hand),
                    Modifier.align(Alignment.CenterVertically)
                )

                IconButton(
                    {
                        val newArgs = form.onCheck()
                        if (newArgs != null) {
                            editingState.value = false

                            if (newArgs != args) {
                                requestChangeArgs(newArgs)
                            }
                        }
                    },
                    Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Outlined.Check, "", tint = MaterialTheme.colorScheme.primary)
                }

                IconButton(
                    {
                        editingState.value = false
                        form.fillFormWithArgs(args)
                    },
                    Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Outlined.Clear, "", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        content = {
            components.Tiles()
        },
    )
}