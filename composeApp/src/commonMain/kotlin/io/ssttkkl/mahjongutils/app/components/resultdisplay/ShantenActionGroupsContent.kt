package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.LazyCardPanel
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.shantenNumText

fun LazyListScope.ShantenActionGroupsContent(
    groups: List<Pair<Int, List<ShantenAction>>>,  // shanten to actions (asc sorted)
    minShantenNum: Int,
) {
    groups.forEach { (shantenNum, actions) ->
        LazyCardPanel(
            items = sequence { yieldAll(actions) },
            keyMapping = { it.toString() },
            header = {
                stringResource(
                    if (shantenNum == minShantenNum)
                        MR.strings.label_shanten_action
                    else
                        MR.strings.label_shanten_action_backwards,
                    shantenNumText(shantenNum)
                )
            },
            content = { action ->
                ShantenActionCardContent(action)
            },
            cardModifier = {
                with(Spacing.current) {
                    Modifier.windowHorizontalMargin()
                }
            },
            titleModifier = {
                with(Spacing.current) {
                    Modifier.windowHorizontalMargin()
                }
            },
        )

        item {
            with(Spacing.current) {
                VerticalSpacerBetweenPanels()
            }
        }
    }
}