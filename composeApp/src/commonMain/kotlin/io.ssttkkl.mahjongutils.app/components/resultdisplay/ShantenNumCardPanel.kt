package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.utils.shantenNumText

@Composable
fun ShantenNumCardPanel(shantenNum: Int) {
    TopCardPanel(Res.string.label_shanten_num, {
        Text(shantenNumText(shantenNum))
    })
}