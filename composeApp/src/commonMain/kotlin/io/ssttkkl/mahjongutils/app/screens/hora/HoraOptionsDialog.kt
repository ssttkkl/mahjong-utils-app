package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.basic.SwitchItem
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.hora.HoraOptions

@Composable
fun HoraOptionsDialog(
    options: HoraOptions,
    onChangeOptions: (HoraOptions) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                Modifier.padding(Spacing.current.cardInnerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                val itemColors = ListItemDefaults.colors(containerColor = Color.Transparent)

                SwitchItem(
                    options.aotenjou,
                    { onChangeOptions(options.copy(aotenjou = it)) },
                    stringResource(MR.strings.label_aotenjou),
                    stringResource(MR.strings.desc_aotenjou),
                    colors = itemColors
                )

                SwitchItem(
                    options.allowKuitan,
                    { onChangeOptions(options.copy(allowKuitan = it)) },
                    stringResource(MR.strings.label_hasKuitan),
                    stringResource(MR.strings.desc_hasKuitan),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasRenpuuJyantouHu,
                    { onChangeOptions(options.copy(hasRenpuuJyantouHu = it)) },
                    stringResource(MR.strings.label_renpuuJyantou4Hu),
                    stringResource(MR.strings.desc_renpuuJyantou4Hu),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasKiriageMangan,
                    { onChangeOptions(options.copy(hasKiriageMangan = it)) },
                    stringResource(MR.strings.label_hasKiriageMangan),
                    stringResource(MR.strings.desc_hasKiriageMangan),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasKazoeYakuman,
                    { onChangeOptions(options.copy(hasKazoeYakuman = it)) },
                    stringResource(MR.strings.label_hasKazoeYakuman),
                    stringResource(MR.strings.desc_hasKazoeYakuman),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasMultipleYakuman,
                    { onChangeOptions(options.copy(hasMultipleYakuman = it)) },
                    stringResource(MR.strings.label_hasMultipleYakuman),
                    stringResource(MR.strings.desc_hasMultipleYakuman),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasComplexYakuman,
                    { onChangeOptions(options.copy(hasComplexYakuman = it)) },
                    stringResource(MR.strings.label_hasComplexYakuman),
                    stringResource(MR.strings.desc_hasComplexYakuman),
                    colors = itemColors
                )

                Row {
                    Surface(Modifier.weight(1f)) {}
                    TextButton({ onChangeOptions(HoraOptions.Default) }) {
                        Text(stringResource(MR.strings.label_hora_options_restore))
                    }
                    TextButton({ onDismissRequest() }) {
                        Text(stringResource(MR.strings.label_hora_options_ok))
                    }
                }
            }
        }
    }
}