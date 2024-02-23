package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_baiman
import mahjongutils.composeapp.generated.resources.label_haneman
import mahjongutils.composeapp.generated.resources.label_hora_point
import mahjongutils.composeapp.generated.resources.label_kazoeyakuman
import mahjongutils.composeapp.generated.resources.label_mangan
import mahjongutils.composeapp.generated.resources.label_sanbaiman
import mahjongutils.composeapp.generated.resources.text_child_ron
import mahjongutils.composeapp.generated.resources.text_child_tsumo
import mahjongutils.composeapp.generated.resources.text_five
import mahjongutils.composeapp.generated.resources.text_four
import mahjongutils.composeapp.generated.resources.text_one
import mahjongutils.composeapp.generated.resources.text_parent_ron
import mahjongutils.composeapp.generated.resources.text_parent_tsumo
import mahjongutils.composeapp.generated.resources.text_six
import mahjongutils.composeapp.generated.resources.text_three
import mahjongutils.composeapp.generated.resources.text_two
import mahjongutils.composeapp.generated.resources.text_x_bai_yakuman
import mahjongutils.composeapp.generated.resources.text_x_han_x_hu
import mahjongutils.composeapp.generated.resources.text_x_han_x_hu_with_tag
import mahjongutils.hanhu.ChildPoint
import mahjongutils.hanhu.ParentPoint
import org.jetbrains.compose.resources.stringResource

@Composable
private fun digitText(digit: Int): String {
    return when (digit) {
        1 -> stringResource(Res.string.text_one)
        2 -> stringResource(Res.string.text_two)
        3 -> stringResource(Res.string.text_three)
        4 -> stringResource(Res.string.text_four)
        5 -> stringResource(Res.string.text_five)
        6 -> stringResource(Res.string.text_six)
        else -> digit.toString()
    }
}

@Composable
private fun hanhuText(han: Int, hu: Int, hasYakuman: Boolean) {
    when {
        hasYakuman -> {
            Text(stringResource(Res.string.text_x_bai_yakuman, digitText(han / 13)))
        }

        han >= 13 -> {
            Text(
                stringResource(
                    Res.string.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(Res.string.label_kazoeyakuman)
                )
            )
        }

        han >= 11 -> {
            Text(
                stringResource(
                    Res.string.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(Res.string.label_sanbaiman)
                )
            )
        }

        han >= 8 -> {
            Text(
                stringResource(
                    Res.string.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(Res.string.label_baiman)
                )
            )
        }

        han >= 6 -> {
            Text(
                stringResource(
                    Res.string.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(Res.string.label_haneman)
                )
            )
        }

        han >= 5 || han == 4 && hu >= 40 || han == 3 && hu >= 70 -> {
            Text(
                stringResource(
                    Res.string.text_x_han_x_hu_with_tag, han, hu,
                    stringResource(Res.string.label_mangan)
                )
            )
        }

        else -> {
            Text(stringResource(Res.string.text_x_han_x_hu, han, hu))
        }
    }
}

@Composable
private fun textParentTsumo(parentPoint: ParentPoint) =
    stringResource(
        Res.string.text_parent_tsumo,
        parentPoint.tsumo.toString(),
        parentPoint.tsumoTotal.toString()
    )

@Composable
private fun textParentRon(parentPoint: ParentPoint) =
    stringResource(
        Res.string.text_parent_ron,
        parentPoint.ron.toString()
    )

@Composable
private fun textChildTsumo(childPoint: ChildPoint) =
    stringResource(
        Res.string.text_child_tsumo,
        childPoint.tsumoChild.toString(),
        childPoint.tsumoParent.toString(),
        childPoint.tsumoTotal.toString(),
    )

@Composable
private fun textChildRon(childPoint: ChildPoint) =
    stringResource(
        Res.string.text_child_ron,
        childPoint.ron.toString()
    )

@Composable
private fun parentPointText(parentPoint: ParentPoint) {
    if (parentPoint.tsumo > 0uL) {
        Text(textParentTsumo(parentPoint))
    }
    if (parentPoint.ron > 0uL) {
        Text(textParentRon(parentPoint))
    }
}

@Composable
private fun childPointText(childPoint: ChildPoint) {
    if (childPoint.tsumoTotal > 0uL) {
        Text(textChildTsumo(childPoint))
    }
    if (childPoint.ron > 0uL) {
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
    TopCardPanel({ Text(stringResource(Res.string.label_hora_point)) }) {
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