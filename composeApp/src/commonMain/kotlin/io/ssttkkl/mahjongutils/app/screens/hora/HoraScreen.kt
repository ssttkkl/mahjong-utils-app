package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.basic.ChooseAction
import io.ssttkkl.mahjongutils.app.components.basic.ComboBox
import io.ssttkkl.mahjongutils.app.components.basic.ComboOption
import io.ssttkkl.mahjongutils.app.components.basic.MultiComboBox
import io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton.SegmentedButtonOption
import io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton.SingleChoiceSegmentedButtonGroup
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.models.hora.HoraCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedName
import io.ssttkkl.mahjongutils.app.utils.withAlpha
import kotlinx.coroutines.launch
import mahjongutils.models.Wind
import mahjongutils.yaku.Yaku

@Composable
private fun ankanOptions(): List<ComboOption<Boolean>> =
    listOf<ComboOption<Boolean>>(
        ComboOption(stringResource(MR.strings.label_ankan), true),
        ComboOption(stringResource(MR.strings.label_minkan), false)
    )

@Composable
private fun windComboOptions(): List<ComboOption<Wind?>> =
    listOf<ComboOption<Wind?>>(
        ComboOption(stringResource(MR.strings.label_wind_unspecified), null),
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

@Composable
private fun tsumoOptions(): List<SegmentedButtonOption<Boolean>> =
    listOf<SegmentedButtonOption<Boolean>>(
        SegmentedButtonOption(stringResource(MR.strings.label_tsumo), true),
        SegmentedButtonOption(stringResource(MR.strings.label_ron), false)
    )

object HoraScreen :
    FormAndResultScreen<HoraScreenModel, HoraCalcResult>() {
    override val title
        get() = MR.strings.title_hora

    override val resultTitle
        get() = MR.strings.title_hora_result

    @Composable
    override fun getScreenModel(): HoraScreenModel {
        return rememberScreenModel { HoraScreenModel() }
    }

    @Composable
    override fun FormContent(
        appState: AppState,
        model: HoraScreenModel,
        modifier: Modifier
    ) {
        val coroutineScope = rememberCoroutineScope()

        with(Spacing.current) {
            Column(
                modifier.verticalScroll(rememberScrollState())
            ) {
                VerticalSpacerBetweenPanels()

                TopPanel(stringResource(MR.strings.label_tiles_in_hand)) {
                    ValidationField(model.tilesErrMsg) { isError ->
                        TileField(
                            value = model.tiles,
                            onValueChange = { model.tiles = it },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isError
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    stringResource(MR.strings.label_furo),
                    noPaddingContent = true
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        model.furo.forEachIndexed { index, furoModel ->
                            ListItem(
                                {
                                    ValidationField(model.furoErrMsg.getOrNull(index)) { isError ->
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
                                        model.furo.removeAt(index)
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
                                model.furo.add(FuroModel())
                            },
                            Modifier.fillMaxWidth().windowHorizontalMargin()
                        ) {
                            Icon(Icons.Filled.Add, "")
                        }
                    }
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    stringResource(MR.strings.label_agari),
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        ValidationField(model.agariErrMsg, Modifier.weight(1f)) { isError ->
                            TileField(
                                value = model.agari?.let { listOf(it) } ?: emptyList(),
                                onValueChange = { model.agari = it.firstOrNull() },
                                modifier = Modifier.fillMaxWidth(),
                                isError = isError
                            )
                        }

                        SingleChoiceSegmentedButtonGroup(
                            tsumoOptions(), model.tsumo, { model.tsumo = it },
                            Modifier.padding(start = 16.dp)
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                Row {
                    TopPanel(
                        stringResource(MR.strings.label_self_wind),
                        Modifier.weight(1f)
                    ) {
                        ComboBox(
                            model.selfWind,
                            { model.selfWind = it },
                            windComboOptions(),
                            Modifier.fillMaxWidth()
                        )
                    }
                    TopPanel(
                        stringResource(MR.strings.label_round_wind),
                        Modifier.weight(1f)
                    ) {
                        ComboBox(
                            model.roundWind, { model.roundWind = it }, windComboOptions(),
                            Modifier.fillMaxWidth()
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                Row {
                    TopPanel(
                        stringResource(MR.strings.label_dora_count),
                        Modifier.weight(1f)
                    ) {
                        ValidationField(model.doraErrMsg) { isError ->
                            OutlinedTextField(
                                value = model.dora,
                                onValueChange = { model.dora = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "0",
                                        style = LocalTextStyle.current.withAlpha(0.4f)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = isError
                            )
                        }
                    }

                    TopPanel(
                        stringResource(MR.strings.label_extra_yaku),
                        Modifier.weight(1f)
                    ) {
                        val allExtraYaku = model.allExtraYaku()
                        val options = yakuComboOptions(allExtraYaku)
                        MultiComboBox(
                            model.extraYaku,
                            {
                                when (it) {
                                    is ChooseAction.OnChoose<Yaku> -> model.extraYaku += it.value
                                    is ChooseAction.OnNotChoose<Yaku> -> model.extraYaku -= it.value
                                }
                            },
                            options,
                            Modifier.fillMaxWidth(),
                            produceDisplayText = {
                                if (it.isEmpty()) {
                                    stringResource(MR.strings.label_extra_yaku_unspecified)
                                } else {
                                    it.joinToString { it.text }
                                }
                            }
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                Button(
                    modifier = Modifier.windowHorizontalMargin(),
                    content = { Text(stringResource(MR.strings.text_calc)) },
                    onClick = {
                        coroutineScope.launch {
                            model.onSubmit(appState)
                        }
                    }
                )

                VerticalSpacerBetweenPanels()
            }
        }
    }

    @Composable
    override fun ResultContent(
        appState: AppState,
        result: HoraCalcResult,
        modifier: Modifier
    ) {

    }

}

