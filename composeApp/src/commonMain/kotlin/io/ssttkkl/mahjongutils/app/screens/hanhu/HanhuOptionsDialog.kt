package io.ssttkkl.mahjongutils.app.screens.hanhu

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
import mahjongutils.composeapp.generated.resources.desc_aotenjou
import mahjongutils.composeapp.generated.resources.desc_hasKazoeYakuman
import mahjongutils.composeapp.generated.resources.desc_hasKiriageMangan
import mahjongutils.composeapp.generated.resources.label_aotenjou
import mahjongutils.composeapp.generated.resources.label_hasKazoeYakuman
import mahjongutils.composeapp.generated.resources.label_hasKiriageMangan
import mahjongutils.composeapp.generated.resources.label_hora_options_ok
import mahjongutils.composeapp.generated.resources.label_hora_options_restore
import mahjongutils.hanhu.HanHuOptions
import org.jetbrains.compose.resources.stringResource

@Composable
fun HanhuOptionsDialog(
    options: HanHuOptions,
    onChangeOptions: (HanHuOptions) -> Unit,
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

                Row {
                    Surface(Modifier.weight(1f)) {}
                    TextButton({ onChangeOptions(HanHuOptions.Default) }) {
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