package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import io.ssttkkl.mahjongutils.app.base.Spacing
import io.ssttkkl.mahjongutils.app.base.components.LazyTopCardPanel
import io.ssttkkl.mahjongutils.app.utils.shantenNumText
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_shanten_action
import mahjongutils.composeapp.generated.resources.label_shanten_action_backwards
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ShantenActionGroupsContent(
    groups: List<Pair<Int, List<ShantenAction>>>,  // shanten to actions (asc sorted)
    minShantenNum: Int,
    fillbackHandler: FillbackHandler,
) {
    groups.forEach { (shantenNum, actions) ->
        LazyTopCardPanel(
            items = sequence { yieldAll(actions) },
            keyMapping = { it.toString() },
            header = {
                Text(
                    stringResource(
                        if (shantenNum == minShantenNum)
                            Res.string.label_shanten_action
                        else
                            Res.string.label_shanten_action_backwards,
                        shantenNumText(shantenNum)
                    )
                )
            },
            content = { action ->
                ShantenActionCardContent(action, fillbackHandler)
            }
        )

        item {
            with(Spacing.current) {
                VerticalSpacerBetweenPanels()
            }
        }
    }
}