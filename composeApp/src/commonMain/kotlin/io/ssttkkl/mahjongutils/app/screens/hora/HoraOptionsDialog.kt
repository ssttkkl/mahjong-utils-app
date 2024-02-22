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
import io.ssttkkl.mahjongutils.app.components.basic.SwitchItem
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.composeapp.generated.resources.Res
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
                    stringResource(Res.string.label_haskuitan),
                    stringResource(Res.string.desc_haskuitan),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasRenpuuJyantouHu,
                    { onChangeOptions(options.copy(hasRenpuuJyantouHu = it)) },
                    stringResource(Res.string.label_renpuujyantou4hu),
                    stringResource(Res.string.desc_renpuujyantou4hu),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasKiriageMangan,
                    { onChangeOptions(options.copy(hasKiriageMangan = it)) },
                    stringResource(Res.string.label_haskiriagemangan),
                    stringResource(Res.string.desc_haskiriagemangan),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasKazoeYakuman,
                    { onChangeOptions(options.copy(hasKazoeYakuman = it)) },
                    stringResource(Res.string.label_haskazoeyakuman),
                    stringResource(Res.string.desc_haskazoeyakuman),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasMultipleYakuman,
                    { onChangeOptions(options.copy(hasMultipleYakuman = it)) },
                    stringResource(Res.string.label_hasmultipleyakuman),
                    stringResource(Res.string.desc_hasmultipleyakuman),
                    colors = itemColors
                )

                SwitchItem(
                    options.hasComplexYakuman,
                    { onChangeOptions(options.copy(hasComplexYakuman = it)) },
                    stringResource(Res.string.label_hascomplexyakuman),
                    stringResource(Res.string.desc_hascomplexyakuman),
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