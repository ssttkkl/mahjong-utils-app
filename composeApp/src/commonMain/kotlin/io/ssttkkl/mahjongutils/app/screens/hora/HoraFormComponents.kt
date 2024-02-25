package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.components.basic.ChooseAction
import io.ssttkkl.mahjongutils.app.components.basic.ComboBox
import io.ssttkkl.mahjongutils.app.components.basic.ComboOption
import io.ssttkkl.mahjongutils.app.components.basic.MultiComboBox
import io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton.SegmentedButtonOption
import io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton.SingleChoiceSegmentedButtonGroup
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import io.ssttkkl.mahjongutils.app.utils.localizedName
import io.ssttkkl.mahjongutils.app.utils.withAlpha
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_agari
import mahjongutils.composeapp.generated.resources.label_ankan
import mahjongutils.composeapp.generated.resources.label_dora_count
import mahjongutils.composeapp.generated.resources.label_extra_yaku
import mahjongutils.composeapp.generated.resources.label_extra_yaku_unspecified
import mahjongutils.composeapp.generated.resources.label_furo
import mahjongutils.composeapp.generated.resources.label_minkan
import mahjongutils.composeapp.generated.resources.label_ron
import mahjongutils.composeapp.generated.resources.label_round_wind
import mahjongutils.composeapp.generated.resources.label_self_wind
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
import mahjongutils.composeapp.generated.resources.label_tsumo
import mahjongutils.composeapp.generated.resources.label_wind_unspecified
import mahjongutils.models.Wind
import mahjongutils.yaku.Yaku
import org.jetbrains.compose.resources.stringResource

class HoraFormComponents(
    val form: HoraFormState
) {
    @Composable
    fun Tiles() {
        ValidationField(form.tilesErrMsg) { isError ->
            TileField(
                value = form.tiles,
                onValueChange = { form.tiles = it },
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                label = stringResource(Res.string.label_tiles_in_hand)
            )
        }
    }

    @Composable
    fun Agari(modifier: Modifier = Modifier) {
        ValidationField(form.agariErrMsg, modifier) { isError ->
            TileField(
                value = form.agari?.let { listOf(it) } ?: emptyList(),
                onValueChange = { form.agari = it.firstOrNull() },
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                label = stringResource(Res.string.label_agari),
                placeholder = {
                    form.autoDetectedAgari?.let { autoDetectedAgari ->
                        io.ssttkkl.mahjongutils.app.components.tile.Tiles(
                            listOf(autoDetectedAgari),
                            Modifier.alpha(0.4f),
                            fontSize = TileTextSize.Default.bodyLarge * 0.8
                        )
                    }
                }
            )
        }
    }

    @Composable
    fun Tsumo() {
        SingleChoiceSegmentedButtonGroup(
            tsumoOptions(), form.tsumo, { form.tsumo = it },
            Modifier.padding(start = 16.dp)
        )
    }

    @Composable
    fun Furo() = with(Spacing.current) {
        ValidationField(form.furoErrMsg) { isError ->
            TopPanel(
                {
                    Text(
                        stringResource(Res.string.label_furo),
                        color = if (isError)
                            MaterialTheme.colorScheme.error
                        else
                            Color.Unspecified
                    )
                },
                noContentPadding = true
            ) {
                Column(Modifier.fillMaxWidth()) {
                    form.furo.forEachIndexed { index, furoModel ->
                        ListItem(
                            {
                                ValidationField(furoModel.errMsg) { isError ->
                                    TileField(
                                        furoModel.tiles,
                                        { furoModel.tiles = it },
                                        Modifier.fillMaxWidth(),
                                        isError = isError
                                    )
                                }
                            },
                            leadingContent = {
                                Icon(Icons.Filled.Close, "", Modifier.clickable {
                                    form.furo.removeAt(index)
                                })
                            },
                            trailingContent = {
                                AnimatedVisibility(
                                    furoModel.isKan,
                                    enter = fadeIn() + expandHorizontally(),
                                    exit = fadeOut() + shrinkHorizontally(),
                                ) {
                                    ComboBox(
                                        furoModel.ankan,
                                        { furoModel.ankan = it },
                                        ankanOptions(),
                                        Modifier.width(150.dp)
                                    )
                                }
                            },
                        )
                    }

                    OutlinedButton(
                        {
                            form.furo.add(FuroModel())
                        },
                        Modifier.fillMaxWidth().windowHorizontalMargin()
                    ) {
                        Icon(Icons.Filled.Add, "")
                    }
                }
            }
        }
    }
    
    @Composable
    fun SelfWind(modifier: Modifier = Modifier) {
        TopPanel(modifier = modifier) {
            ComboBox(
                form.selfWind,
                { form.selfWind = it },
                windComboOptions(),
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.label_self_wind)) }
            )
        }
    }
    
    @Composable
    fun RoundWind(modifier: Modifier = Modifier) {
        TopPanel(modifier = modifier) {
            ComboBox(
                form.roundWind, { form.roundWind = it }, windComboOptions(),
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.label_round_wind)) }
            )
        }
    }
    
    @Composable
    fun Dora() {
        ValidationField(form.doraErrMsg) { isError ->
            OutlinedTextField(
                value = form.dora,
                onValueChange = { form.dora = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "0",
                        style = LocalTextStyle.current.withAlpha(0.4f)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError,
                label = { Text(stringResource(Res.string.label_dora_count)) }
            )
        }
    }
    
    @Composable
    fun ExtraYaku() {
        val options = yakuComboOptions(form.allExtraYaku)

        // 某些役种被ban后更新选择
        LaunchedEffect(form.unavailableYaku) {
            form.extraYaku -= form.unavailableYaku
        }

        MultiComboBox(
            form.extraYaku,
            {
                when (it) {
                    is ChooseAction.OnChoose<Yaku> -> form.extraYaku += it.value
                    is ChooseAction.OnNotChoose<Yaku> -> form.extraYaku -= it.value
                }
            },
            options,
            Modifier.fillMaxWidth(),
            produceDisplayText = {
                if (it.isEmpty()) {
                    stringResource(Res.string.label_extra_yaku_unspecified)
                } else {
                    it.joinToString { it.text }
                }
            },
            label = { Text(stringResource(Res.string.label_extra_yaku)) }
        )
    }

    @Composable
    fun HoraOptionsDialog(onDismissRequest:()->Unit) {
        HoraOptionsDialog(
            form.horaOptions,
            onChangeOptions = {
                form.horaOptions = it
            },
            onDismissRequest = onDismissRequest,
        )
    }

    companion object {
        @Composable
        private fun tsumoOptions(): List<SegmentedButtonOption<Boolean>> =
            listOf<SegmentedButtonOption<Boolean>>(
                SegmentedButtonOption(stringResource(Res.string.label_tsumo), true),
                SegmentedButtonOption(stringResource(Res.string.label_ron), false)
            )

        @Composable
        private fun ankanOptions(): List<ComboOption<Boolean>> =
            listOf<ComboOption<Boolean>>(
                ComboOption(stringResource(Res.string.label_ankan), true),
                ComboOption(stringResource(Res.string.label_minkan), false)
            )
        @Composable
        private fun windComboOptions(): List<ComboOption<Wind?>> =
            listOf<ComboOption<Wind?>>(
                ComboOption(stringResource(Res.string.label_wind_unspecified), null),
                ComboOption(stringResource(Wind.East.localizedName), Wind.East),
                ComboOption(stringResource(Wind.South.localizedName), Wind.South),
                ComboOption(stringResource(Wind.West.localizedName), Wind.West),
                ComboOption(stringResource(Wind.North.localizedName), Wind.North)
            )

        @Composable
        private fun yakuComboOptions(allExtraYaku: List<Pair<Yaku, Boolean>>) =
            allExtraYaku
                .map {
                    ComboOption(stringResource(it.first.localizedName), it.first, it.second)
                }

    }
}