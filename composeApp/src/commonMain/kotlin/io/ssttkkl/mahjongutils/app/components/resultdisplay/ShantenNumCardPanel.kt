package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.utils.shantenNumText

@Composable
fun ShantenNumCardPanel(shantenNum: Int) {
    TopCardPanel(stringResource(MR.strings.label_shanten_num)) {
        Text(shantenNumText(shantenNum))
    }
}