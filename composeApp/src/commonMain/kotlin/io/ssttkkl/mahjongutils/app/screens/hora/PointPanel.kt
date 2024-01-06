package io.ssttkkl.mahjongutils.app.screens.hora

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
import mahjongutils.hora.Hora
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
private fun hanhuText(hora: Hora) {
    when {
        hora.hasYakuman -> {
            Text(stringResource(MR.strings.text_x_bai_yakuman, digitText(hora.han / 13)))
        }
        hora.han >= 13 -> {
            Text(stringResource(MR.strings.text_x_han_x_hu_with_tag, hora.han, hora.hu,
                stringResource(MR.strings.label_kazoeyakuman)
            ))
        }
        hora.han >= 11 -> {
            Text(stringResource(MR.strings.text_x_han_x_hu_with_tag, hora.han, hora.hu,
                stringResource(MR.strings.label_sanbaiman)
            ))
        }
        hora.han >= 8 -> {
            Text(stringResource(MR.strings.text_x_han_x_hu_with_tag, hora.han, hora.hu,
                stringResource(MR.strings.label_baiman)
            ))
        }
        hora.han >= 6 -> {
            Text(stringResource(MR.strings.text_x_han_x_hu_with_tag, hora.han, hora.hu,
                stringResource(MR.strings.label_haneman)
            ))
        }
        hora.han >= 5 || hora.han == 4 && hora.hu >= 40 || hora.han == 3 && hora.hu >= 70 -> {
            Text(stringResource(MR.strings.text_x_han_x_hu_with_tag, hora.han, hora.hu,
                stringResource(MR.strings.label_mangan)
            ))
        }
        else -> {
            Text(stringResource(MR.strings.text_x_han_x_hu, hora.han, hora.hu))
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
private fun parentPointText(hora: Hora) {
    if (hora.tsumo) {
        Text(
            when (hora.parentPoint.tsumo) {
                ParentPoint.Mangan.tsumo ->
                    textParentTsumo(
                        hora.parentPoint
                    )

                ParentPoint.Haneman.tsumo ->
                    textParentTsumo(
                        hora.parentPoint
                    )

                ParentPoint.Baiman.tsumo ->
                    textParentTsumo(
                        hora.parentPoint
                    )

                ParentPoint.Sanbaiman.tsumo ->
                    textParentTsumo(
                        hora.parentPoint
                    )

                ParentPoint.Yakuman.tsumo ->
                    textParentTsumo(
                        hora.parentPoint
                    )

                else -> {
                    if (hora.parentPoint.tsumo > ParentPoint.Yakuman.tsumo) {
                        textParentTsumo(
                            hora.parentPoint
                        )
                    } else {
                        textParentTsumo(hora.parentPoint)
                    }
                }
            }
        )
    } else {
        Text(
            when (hora.parentPoint.ron) {
                ParentPoint.Mangan.ron ->
                    textParentRon(
                        hora.parentPoint
                    )

                ParentPoint.Haneman.ron ->
                    textParentRon(
                        hora.parentPoint
                    )

                ParentPoint.Baiman.ron ->
                    textParentRon(
                        hora.parentPoint
                    )

                ParentPoint.Sanbaiman.ron ->
                    textParentRon(
                        hora.parentPoint
                    )

                ParentPoint.Yakuman.ron ->
                    textParentRon(
                        hora.parentPoint
                    )

                else -> {
                    if (hora.parentPoint.ron > ParentPoint.Yakuman.ron) {
                        textParentRon(
                            hora.parentPoint
                        )
                    } else {
                        textParentRon(hora.parentPoint)
                    }
                }
            }
        )
    }
}

@Composable
private fun childPointText(hora: Hora) {
    if (hora.tsumo) {
        Text(
            when (hora.childPoint.tsumoTotal) {
                ChildPoint.Mangan.tsumoTotal ->
                    textChildTsumo(
                        hora.childPoint
                    )

                ChildPoint.Haneman.tsumoTotal ->
                    textChildTsumo(
                        hora.childPoint
                    )

                ChildPoint.Baiman.tsumoTotal ->
                    textChildTsumo(
                        hora.childPoint
                    )

                ChildPoint.Sanbaiman.tsumoTotal ->
                    textChildTsumo(
                        hora.childPoint
                    )

                ChildPoint.Yakuman.tsumoTotal ->
                    textChildTsumo(
                        hora.childPoint
                    )

                else -> {
                    if (hora.childPoint.tsumoTotal > ChildPoint.Yakuman.tsumoTotal) {
                        textChildTsumo(
                            hora.childPoint
                        )
                    } else {
                        textChildTsumo(hora.childPoint)
                    }
                }
            }
        )
    } else {
        Text(
            when (hora.childPoint.ron) {
                ChildPoint.Mangan.ron ->
                    textChildRon(
                        hora.childPoint
                    )

                ChildPoint.Haneman.ron ->
                    textChildRon(
                        hora.childPoint
                    )

                ChildPoint.Baiman.ron ->
                    textChildRon(
                        hora.childPoint
                    )

                ChildPoint.Sanbaiman.ron ->
                    textChildRon(
                        hora.childPoint
                    )

                ChildPoint.Yakuman.ron ->
                    textChildRon(
                        hora.childPoint
                    )

                else -> {
                    if (hora.childPoint.ron > ChildPoint.Yakuman.ron) {
                        textChildRon(
                            hora.childPoint
                        )
                    } else {
                        textChildRon(hora.childPoint)
                    }
                }
            }
        )
    }
}

@Composable
internal fun PointPanel(hora: Hora) {
    TopCardPanel({ Text(stringResource(MR.strings.label_hora_point)) }) {
        hanhuText(hora)
    }
    Spacer(Modifier.height(8.dp))
    if (hora.selfWind == null) {
        TopCardPanel {
            parentPointText(hora)
        }
        Spacer(Modifier.height(8.dp))
        TopCardPanel {
            childPointText(hora)
        }
    } else if (hora.selfWind == Wind.East) {
        TopCardPanel {
            parentPointText(hora)
        }
    } else {
        TopCardPanel {
            childPointText(hora)
        }
    }
}