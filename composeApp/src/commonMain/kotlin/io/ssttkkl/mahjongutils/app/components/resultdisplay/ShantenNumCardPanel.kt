package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.utils.shantenNumText
import mahjongutils.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShantenNumCardPanel(shantenNum: Int) {
    TopCardPanel({ Text(stringResource(Res.string.label_shanten_num)) }) {
        Text(shantenNumText(shantenNum))
    }
}