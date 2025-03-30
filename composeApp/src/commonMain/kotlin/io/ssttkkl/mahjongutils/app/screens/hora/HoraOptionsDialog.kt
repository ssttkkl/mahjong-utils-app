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
import io.ssttkkl.mahjongutils.app.base.components.SwitchItem
import io.ssttkkl.mahjongutils.app.base.Spacing
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.desc_aotenjou
import mahjongutils.composeapp.generated.resources.desc_hasComplexYakuman
import mahjongutils.composeapp.generated.resources.desc_hasKazoeYakuman
import mahjongutils.composeapp.generated.resources.desc_hasKiriageMangan
import mahjongutils.composeapp.generated.resources.desc_hasKuitan
import mahjongutils.composeapp.generated.resources.desc_hasMultipleYakuman
import mahjongutils.composeapp.generated.resources.desc_renpuuJyantou4Hu
import mahjongutils.composeapp.generated.resources.label_aotenjou
import mahjongutils.composeapp.generated.resources.label_hasComplexYakuman
import mahjongutils.composeapp.generated.resources.label_hasKazoeYakuman
import mahjongutils.composeapp.generated.resources.label_hasKiriageMangan
import mahjongutils.composeapp.generated.resources.label_hasKuitan
import mahjongutils.composeapp.generated.resources.label_hasMultipleYakuman
import mahjongutils.composeapp.generated.resources.label_hora_options_ok
import mahjongutils.composeapp.generated.resources.label_hora_options_restore
import mahjongutils.composeapp.generated.resources.label_renpuuJyantou4Hu
import mahjongutils.hora.HoraOptions
import org.jetbrains.compose.resources.stringResource

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
                    stringResource(Res.string.label_aotenjou),
                    stringResource(Res.string.desc_aotenjou),
                    colors = itemColors
                )

                SwitchItem(
                    options.allowKuitan,
                    { onChangeOptions(options.copy(allowKuitan = it)) },
                    stringResource(Res.string.label_hasKuitan),
                    stringResource(Res.string.desc_hasKuitan),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasRenpuuJyantouHu,
                    { onChangeOptions(options.copy(hasRenpuuJyantouHu = it)) },
                    stringResource(Res.string.label_renpuuJyantou4Hu),
                    stringResource(Res.string.desc_renpuuJyantou4Hu),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasKiriageMangan,
                    { onChangeOptions(options.copy(hasKiriageMangan = it)) },
                    stringResource(Res.string.label_hasKiriageMangan),
                    stringResource(Res.string.desc_hasKiriageMangan),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasKazoeYakuman,
                    { onChangeOptions(options.copy(hasKazoeYakuman = it)) },
                    stringResource(Res.string.label_hasKazoeYakuman),
                    stringResource(Res.string.desc_hasKazoeYakuman),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasMultipleYakuman,
                    { onChangeOptions(options.copy(hasMultipleYakuman = it)) },
                    stringResource(Res.string.label_hasMultipleYakuman),
                    stringResource(Res.string.desc_hasMultipleYakuman),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasComplexYakuman,
                    { onChangeOptions(options.copy(hasComplexYakuman = it)) },
                    stringResource(Res.string.label_hasComplexYakuman),
                    stringResource(Res.string.desc_hasComplexYakuman),
                    colors = itemColors
                )

                Row {
                    Surface(Modifier.weight(1f)) {}
                    TextButton({ onChangeOptions(HoraOptions.Default) }) {
                        Text(stringResource(Res.string.label_hora_options_restore))
                    }
                    TextButton({ onDismissRequest() }) {
                        Text(stringResource(Res.string.label_hora_options_ok))
                    }
                }
            }
        }
    }
}