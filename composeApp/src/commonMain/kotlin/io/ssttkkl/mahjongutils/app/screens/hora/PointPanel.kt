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
private fun hanhuText(hora: Hora) {
    if (hora.hasYakuman) {
        Text(stringResource(MR.strings.text_x_bai_yakuman, hora.han / 13))
    } else {
        Text(stringResource(MR.strings.text_x_han_x_hu, hora.han, hora.hu))
    }
}

@Composable
private fun textParentTsumo(parentPoint: ParentPoint, tag: String? = null) =
    if (tag != null)
        stringResource(
            MR.strings.text_parent_tsumo_with_tag,
            parentPoint.tsumo,
            parentPoint.tsumoTotal,
            tag
        )
    else
        stringResource(
            MR.strings.text_parent_tsumo,
            parentPoint.tsumo,
            parentPoint.tsumoTotal
        )

@Composable
private fun textParentRon(parentPoint: ParentPoint, tag: String? = null) =
    if (tag != null)
        stringResource(
            MR.strings.text_parent_ron_with_tag,
            parentPoint.ron,
            tag
        )
    else
        stringResource(
            MR.strings.text_parent_ron,
            parentPoint.ron
        )

@Composable
private fun textChildTsumo(childPoint: ChildPoint, tag: String? = null) =
    if (tag != null)
        stringResource(
            MR.strings.text_child_tsumo_with_tag,
            childPoint.tsumoChild,
            childPoint.tsumoParent,
            childPoint.tsumoTotal,
            tag
        )
    else
        stringResource(
            MR.strings.text_child_tsumo,
            childPoint.tsumoChild,
            childPoint.tsumoParent,
            childPoint.tsumoTotal,
        )

@Composable
private fun textChildRon(childPoint: ChildPoint, tag: String? = null) =
    if (tag != null)
        stringResource(
            MR.strings.text_child_ron_with_tag,
            childPoint.ron,
            tag
        )
    else
        stringResource(
            MR.strings.text_child_ron,
            childPoint.ron
        )

@Composable
private fun parentPointText(hora: Hora) {
    if (hora.tsumo) {
        Text(
            when (hora.parentPoint) {
                ParentPoint.Mangan ->
                    textParentTsumo(
                        hora.parentPoint,
                        stringResource(MR.strings.label_mangan)
                    )

                ParentPoint.Haneman ->
                    textParentTsumo(
                        hora.parentPoint,
                        stringResource(MR.strings.label_haneman)
                    )

                ParentPoint.Baiman ->
                    textParentTsumo(
                        hora.parentPoint,
                        stringResource(MR.strings.label_baiman)
                    )

                ParentPoint.Sanbaiman ->
                    textParentTsumo(
                        hora.parentPoint,
                        stringResource(MR.strings.label_sanbaiman)
                    )

                ParentPoint.Yakuman ->
                    textParentTsumo(
                        hora.parentPoint,
                        if (hora.hasYakuman) stringResource(MR.strings.label_yakuman)
                        else stringResource(MR.strings.label_kazoeyakuman)
                    )

                else -> {
                    if (hora.parentPoint.tsumoTotal > ParentPoint.Yakuman.tsumoTotal) {
                        textParentTsumo(
                            hora.parentPoint,
                            stringResource(MR.strings.label_multiple_yakuman, hora.han / 13)
                        )
                    } else {
                        textParentTsumo(hora.parentPoint)
                    }
                }
            }
        )
    } else {
        Text(
            when (hora.parentPoint) {
                ParentPoint.Mangan ->
                    textParentRon(
                        hora.parentPoint,
                        stringResource(MR.strings.label_mangan)
                    )

                ParentPoint.Haneman ->
                    textParentRon(
                        hora.parentPoint,
                        stringResource(MR.strings.label_haneman)
                    )

                ParentPoint.Baiman ->
                    textParentRon(
                        hora.parentPoint,
                        stringResource(MR.strings.label_baiman)
                    )

                ParentPoint.Sanbaiman ->
                    textParentRon(
                        hora.parentPoint,
                        stringResource(MR.strings.label_sanbaiman)
                    )

                ParentPoint.Yakuman ->
                    textParentRon(
                        hora.parentPoint,
                        if (hora.hasYakuman) stringResource(MR.strings.label_yakuman)
                        else stringResource(MR.strings.label_kazoeyakuman)
                    )

                else -> {
                    if (hora.parentPoint.tsumoTotal > ParentPoint.Yakuman.tsumoTotal) {
                        textParentRon(
                            hora.parentPoint,
                            stringResource(MR.strings.label_multiple_yakuman, hora.han / 13)
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
            when (hora.childPoint) {
                ChildPoint.Mangan ->
                    textChildTsumo(
                        hora.childPoint,
                        stringResource(MR.strings.label_mangan)
                    )

                ChildPoint.Haneman ->
                    textChildTsumo(
                        hora.childPoint,
                        stringResource(MR.strings.label_haneman)
                    )

                ChildPoint.Baiman ->
                    textChildTsumo(
                        hora.childPoint,
                        stringResource(MR.strings.label_baiman)
                    )

                ChildPoint.Sanbaiman ->
                    textChildTsumo(
                        hora.childPoint,
                        stringResource(MR.strings.label_sanbaiman)
                    )

                ChildPoint.Yakuman ->
                    textChildTsumo(
                        hora.childPoint,
                        if (hora.hasYakuman) stringResource(MR.strings.label_yakuman)
                        else stringResource(MR.strings.label_kazoeyakuman)
                    )

                else -> {
                    if (hora.childPoint.tsumoTotal > ChildPoint.Yakuman.tsumoTotal) {
                        textChildTsumo(
                            hora.childPoint,
                            stringResource(MR.strings.label_multiple_yakuman, hora.han / 13)
                        )
                    } else {
                        textChildTsumo(hora.childPoint)
                    }
                }
            }
        )
    } else {
        Text(
            when (hora.childPoint) {
                ChildPoint.Mangan ->
                    textChildRon(
                        hora.childPoint,
                        stringResource(MR.strings.label_mangan)
                    )

                ChildPoint.Haneman ->
                    textChildRon(
                        hora.childPoint,
                        stringResource(MR.strings.label_haneman)
                    )

                ChildPoint.Baiman ->
                    textChildRon(
                        hora.childPoint,
                        stringResource(MR.strings.label_baiman)
                    )

                ChildPoint.Sanbaiman ->
                    textChildRon(
                        hora.childPoint,
                        stringResource(MR.strings.label_sanbaiman)
                    )

                ChildPoint.Yakuman ->
                    textChildRon(
                        hora.childPoint,
                        if (hora.hasYakuman) stringResource(MR.strings.label_yakuman)
                        else stringResource(MR.strings.label_kazoeyakuman)
                    )

                else -> {
                    if (hora.childPoint.tsumoTotal > ChildPoint.Yakuman.tsumoTotal) {
                        textChildRon(
                            hora.childPoint,
                            stringResource(MR.strings.label_multiple_yakuman, hora.han / 13)
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