package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.checkbox.CheckboxWithText
import io.ssttkkl.mahjongutils.app.components.combobox.ComboBox
import io.ssttkkl.mahjongutils.app.components.combobox.ComboOption
import io.ssttkkl.mahjongutils.app.components.combobox.MultiComboBox
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedName
import kotlinx.coroutines.launch
import mahjongutils.models.Wind


private val windComboOptions = listOf<ComboOption<Wind?>>(
    ComboOption(Res.string.label_wind_unspecified, null),
    ComboOption(Wind.East.localizedName, Wind.East),
    ComboOption(Wind.South.localizedName, Wind.South),
    ComboOption(Wind.West.localizedName, Wind.West),
    ComboOption(Wind.North.localizedName, Wind.North)
)

object HoraScreen :
    FormAndResultScreen<HoraScreenModel, HoraCalcResult>() {
    override val title: String
        get() = Res.string.title_hora

    override val resultTitle: String
        get() = Res.string.title_hora_result

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

                TopPanel(Res.string.label_tiles_in_hand) {
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
                    Res.string.label_furo,
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
                                        CheckboxWithText(
                                            furoModel.ankan,
                                            { furoModel.ankan = !furoModel.ankan },
                                            Modifier.fillMaxHeight()
                                        ) {
                                            Text(Res.string.label_ankan)
                                        }
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
                    Res.string.label_agari,
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
                        CheckboxWithText(
                            model.tsumo,
                            { model.tsumo = it },
                            Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                Res.string.label_tsumo,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                VerticalSpacerBetweenPanels()

                Row {
                    TopPanel(
                        Res.string.label_self_wind,
                        Modifier.weight(1f)
                    ) {
                        ComboBox(
                            model.selfWind,
                            { model.selfWind = it },
                            windComboOptions,
                            Modifier.fillMaxWidth()
                        )
                    }
                    TopPanel(
                        Res.string.label_round_wind,
                        Modifier.weight(1f)
                    ) {
                        ComboBox(
                            model.roundWind, { model.roundWind = it }, windComboOptions,
                            Modifier.fillMaxWidth()
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                Row {
                    TopPanel(
                        Res.string.label_dora,
                        Modifier.weight(1f)
                    ) {
                        ValidationField(model.doraErrMsg) { isError ->
                            OutlinedTextField(
                                value = model.dora,
                                onValueChange = { model.dora = it },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = isError
                            )
                        }
                    }

                    TopPanel(
                        Res.string.label_extra_yaku,
                        Modifier.weight(1f)
                    ) {
                        val allExtraYaku = model.allExtraYaku()
                        val availableExtraYaku = model.availableExtraYaku()
                        val options = allExtraYaku.map {
                            ComboOption(it.name, it, it in availableExtraYaku)
                        }
                        MultiComboBox(
                            model.extraYaku,
                            { _, it -> model.extraYaku = it },
                            options,
                            Modifier.fillMaxWidth()
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                Button(
                    modifier = Modifier.windowHorizontalMargin(),
                    content = { Text(Res.string.text_calc) },
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

