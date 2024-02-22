package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import io.ssttkkl.mahjongutils.app.components.panel.LazyTopCardPanel
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.shantenNumText
import mahjongutils.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ShantenActionGroupsContent(
    groups: List<Pair<Int, List<ShantenAction>>>,  // shanten to actions (asc sorted)
    minShantenNum: Int,
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
                ShantenActionCardContent(action)
            }
        )

        item {
            with(Spacing.current) {
                VerticalSpacerBetweenPanels()
            }
        }
    }
}