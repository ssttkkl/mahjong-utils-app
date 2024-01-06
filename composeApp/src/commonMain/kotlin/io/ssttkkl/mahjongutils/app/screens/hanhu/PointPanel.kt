package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import mahjongutils.hanhu.ChildPoint
import mahjongutils.hanhu.ParentPoint
import mahjongutils.models.Wind

@Composable
private fun digitText(digit: Int): String {
    return when (digit) {
        1 -> stringResource(MR.strings.text_one)
        2 -> stringResource(MR.strings.text_two)
        3 -> stringResource(MR.strings.text_three)
        4 -> stringResource(MR.strings.text_four)
        5 -> stringResource(MR.strings.text_five)
        6 -> stringResource(MR.strings.text_six)
        else -> digit.toString()
    }
}

@Composable
private fun hanhuText(han: Int, hu: Int, hasYakuman: Boolean) {
    when {
        hasYakuman -> {
            Text(stringResource(MR.strings.text_x_bai_yakuman, digitText(han / 13)))
        }

        han >= 13 -> {
            Text(
                stringResource(
                    MR.strings.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(MR.strings.label_kazoeyakuman)
                )
            )
        }

        han >= 11 -> {
            Text(
                stringResource(
                    MR.strings.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(MR.strings.label_sanbaiman)
                )
            )
        }

        han >= 8 -> {
            Text(
                stringResource(
                    MR.strings.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(MR.strings.label_baiman)
                )
            )
        }

        han >= 6 -> {
            Text(
                stringResource(
                    MR.strings.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(MR.strings.label_haneman)
                )
            )
        }

        han >= 5 || han == 4 && hu >= 40 || han == 3 && hu >= 70 -> {
            Text(
                stringResource(
                    MR.strings.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(MR.strings.label_mangan)
                )
            )
        }

        else -> {
            Text(stringResource(MR.strings.text_x_han_x_hu, han, hu))
        }
    }
}

@Composable
private fun textParentTsumo(parentPoint: ParentPoint) =
    stringResource(
        MR.strings.text_parent_tsumo,
        parentPoint.tsumo,
        parentPoint.tsumoTotal
    )

@Composable
private fun textParentRon(parentPoint: ParentPoint) =
    stringResource(
        MR.strings.text_parent_ron,
        parentPoint.ron
    )

@Composable
private fun textChildTsumo(childPoint: ChildPoint) =
    stringResource(
        MR.strings.text_child_tsumo,
        childPoint.tsumoChild,
        childPoint.tsumoParent,
        childPoint.tsumoTotal,
    )

@Composable
private fun textChildRon(childPoint: ChildPoint) =
    stringResource(
        MR.strings.text_child_ron,
        childPoint.ron
    )

@Composable
private fun parentPointText(parentPoint: ParentPoint) {
    if (parentPoint.tsumo > 0) {
        Text(textParentTsumo(parentPoint))
    }
    if (parentPoint.ron > 0) {
        Text(textParentRon(parentPoint))
    }
}

@Composable
private fun childPointText(childPoint: ChildPoint) {
    if (childPoint.tsumoTotal > 0) {
        Text(textChildTsumo(childPoint))
    }
    if (childPoint.ron > 0) {
        Text(textChildRon(childPoint))
    }
}

@Composable
fun PointPanel(
    han: Int,
    hu: Int,
    hasYakuman: Boolean,
    parentPoint: ParentPoint?,
    childPoint: ChildPoint?
) {
    TopCardPanel({ Text(stringResource(MR.strings.label_hora_point)) }) {
        hanhuText(han, hu, hasYakuman)
    }
    Spacer(Modifier.height(8.dp))
    when {
        parentPoint != null && childPoint != null -> {
            TopCardPanel {
                parentPointText(parentPoint)
            }
            Spacer(Modifier.height(8.dp))
            TopCardPanel {
                childPointText(childPoint)
            }
        }

        parentPoint != null -> {
            TopCardPanel {
                parentPointText(parentPoint)
            }
        }

        childPoint != null -> {
            TopCardPanel {
                childPointText(childPoint)
            }
        }
    }
}