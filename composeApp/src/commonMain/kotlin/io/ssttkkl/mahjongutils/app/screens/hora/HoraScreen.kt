package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppDialogState
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.basic.ChooseAction
import io.ssttkkl.mahjongutils.app.components.basic.ComboBox
import io.ssttkkl.mahjongutils.app.components.basic.ComboOption
import io.ssttkkl.mahjongutils.app.components.basic.MultiComboBox
import io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton.SegmentedButtonOption
import io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton.SingleChoiceSegmentedButtonGroup
import io.ssttkkl.mahjongutils.app.components.panel.Caption
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.tile.Tiles
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.models.hora.HoraCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.TileTextSize
import io.ssttkkl.mahjongutils.app.utils.localizedFormatting
import io.ssttkkl.mahjongutils.app.utils.localizedName
import io.ssttkkl.mahjongutils.app.utils.withAlpha
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.models.Wind
import mahjongutils.yaku.Yaku
import org.jetbrains.compose.resources.stringResource

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

@Composable
private fun tsumoOptions(): List<SegmentedButtonOption<Boolean>> =
    listOf<SegmentedButtonOption<Boolean>>(
        SegmentedButtonOption(stringResource(Res.string.label_tsumo), true),
        SegmentedButtonOption(stringResource(Res.string.label_ron), false)
    )


object HoraScreen :
    FormAndResultScreen<HoraScreenModel, HoraArgs, HoraCalcResult>() {
    override val title
        get() = Res.string.title_hora

    override val resultTitle
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
        val verticalScrollState = rememberScrollState()

        with(Spacing.current) {
            ScrollBox(verticalScrollState = verticalScrollState, modifier = modifier) {
                Column(
                    Modifier.verticalScroll(verticalScrollState)
                ) {
                    VerticalSpacerBetweenPanels()

                    TopPanel {
                        ValidationField(model.tilesErrMsg) { isError ->
                            TileField(
                                value = model.tiles,
                                onValueChange = { model.tiles = it },
                                modifier = Modifier.fillMaxWidth(),
                                isError = isError,
                                label = stringResource(Res.string.label_tiles_in_hand)
                            )
                        }
                    }

                    VerticalSpacerBetweenPanels()

                    TopPanel {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ValidationField(model.agariErrMsg, Modifier.weight(1f)) { isError ->
                                TileField(
                                    value = model.agari?.let { listOf(it) } ?: emptyList(),
                                    onValueChange = { model.agari = it.firstOrNull() },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = isError,
                                    label = stringResource(Res.string.label_agari),
                                    placeholder = {
                                        model.autoDetectedAgari?.let { autoDetectedAgari ->
                                            Tiles(
                                                listOf(autoDetectedAgari),
                                                Modifier.alpha(0.4f),
                                                fontSize = TileTextSize.Default.bodyLarge * 0.8
                                            )
                                        }
                                    }
                                )
                            }

                            SingleChoiceSegmentedButtonGroup(
                                tsumoOptions(), model.tsumo, { model.tsumo = it },
                                Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    VerticalSpacerBetweenPanels()

                    TopPanel(
                        { Text(stringResource(Res.string.label_furo)) },
                        noContentPadding = true
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            model.furo.forEachIndexed { index, furoModel ->
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
                        { Text(stringResource(Res.string.label_other_information)) },
                        noContentPadding = true
                    ) {
                        Row {
                            TopPanel(modifier = Modifier.weight(1f)) {
                                ComboBox(
                                    model.selfWind,
                                    { model.selfWind = it },
                                    windComboOptions(),
                                    Modifier.fillMaxWidth(),
                                    label = { Text(stringResource(Res.string.label_self_wind)) }
                                )
                            }
                            TopPanel(modifier = Modifier.weight(1f)) {
                                ComboBox(
                                    model.roundWind, { model.roundWind = it }, windComboOptions(),
                                    Modifier.fillMaxWidth(),
                                    label = { Text(stringResource(Res.string.label_round_wind)) }
                                )
                            }
                        }

                        VerticalSpacerBetweenPanels()

                        Row {
                            TopPanel(modifier = Modifier.weight(1f)) {
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
                                        isError = isError,
                                        label = { Text(stringResource(Res.string.label_dora_count)) }
                                    )
                                }
                            }

                            TopPanel(modifier = Modifier.weight(1f)) {
                                val options = yakuComboOptions(model.allExtraYaku)

                                // 某些役种被ban后更新选择
                                LaunchedEffect(model.unavailableYaku) {
                                    model.extraYaku -= model.unavailableYaku
                                }

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
                                            stringResource(Res.string.label_extra_yaku_unspecified)
                                        } else {
                                            it.joinToString { it.text }
                                        }
                                    },
                                    label = { Text(stringResource(Res.string.label_extra_yaku)) }
                                )
                            }
                        }
                    }

                    VerticalSpacerBetweenPanels()

                    var optionsDialogVisible by rememberSaveable { mutableStateOf(false) }
                    if (optionsDialogVisible) {
                        HoraOptionsDialog(
                            model.horaOptions,
                            onChangeOptions = {
                                model.horaOptions = it
                            },
                            onDismissRequest = {
                                optionsDialogVisible = false
                            },
                        )
                    }

                    Row {
                        Button(
                            modifier = Modifier.windowHorizontalMargin(),
                            content = { Text(stringResource(Res.string.label_calc)) },
                            onClick = {
                                model.onSubmit()
                            }
                        )

                        TextButton({ optionsDialogVisible = true }) {
                            Text(stringResource(Res.string.label_hora_options))
                        }
                    }

                    VerticalSpacerBetweenPanels()
                }
            }
        }
    }

    @Composable
    override fun ResultContent(
        appState: AppState,
        result: HoraCalcResult,
        modifier: Modifier
    ) {
        HoraResultContent(result.args, result.result)
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun HistoryItem(item: History<HoraArgs>, model: HoraScreenModel) {
        Column {
            HoraTiles(item.args)

            Spacer(Modifier.height(8.dp))

            FlowRow {
                Caption(title = {
                    if (item.args.tsumo) {
                        Text(stringResource(Res.string.label_tsumo))
                    } else {
                        Text(stringResource(Res.string.label_ron))
                    }
                })

                if (item.args.selfWind != null) {
                    Spacer(Modifier.width(24.dp))
                    Caption(
                        title = { Text(stringResource(Res.string.label_self_wind)) },
                        content = { Text(stringResource(item.args.selfWind.localizedName)) }
                    )
                }

                if (item.args.roundWind != null) {
                    Spacer(Modifier.width(24.dp))
                    Caption(
                        title = { Text(stringResource(Res.string.label_round_wind)) },
                        content = { Text(stringResource(item.args.roundWind.localizedName)) }
                    )
                }

                if (item.args.dora > 0) {
                    Spacer(Modifier.width(24.dp))
                    Caption(
                        title = { Text(stringResource(Res.string.label_dora_count)) },
                        content = { Text(item.args.dora.toString()) }
                    )
                }

                if (item.args.extraYaku.isNotEmpty()) {
                    Spacer(Modifier.width(24.dp))
                    Caption(
                        title = { Text(stringResource(Res.string.label_extra_yaku)) },
                        content = {
                            Text(
                                item.args.extraYaku.map { stringResource(it.localizedName) }
                                    .joinToString(stringResource(Res.string.text_comma))
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                item.createTime.localizedFormatting(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    override fun onClickHistoryItem(
        item: History<HoraArgs>,
        model: HoraScreenModel,
        appState: AppState
    ) {
        with(item.args) {
            model.tiles = tiles
            model.furo.clear()
            model.furo.addAll(furo.map(FuroModel::fromFuro))
            model.agari = agari
            model.tsumo = tsumo
            model.dora = dora.toString()
            model.selfWind = selfWind
            model.roundWind = roundWind
            model.extraYaku = extraYaku

            if (model.horaOptions != options) {
                appState.appDialogState = AppDialogState { onDismissRequest ->
                    OverwriteHoraOptionsAlertDialog(onDismissRequest) {
                        model.horaOptions = options
                    }
                }.also {
                    it.visible = true
                }
            }
        }

        model.postCheck()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverwriteHoraOptionsAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card {
            Column(
                Modifier.padding(Spacing.current.cardInnerPadding)
            ) {
                Text(stringResource(Res.string.text_overwrite_hora_options_hint))

                Row {
                    Surface(Modifier.weight(1f)) {}
                    TextButton({ onDismissRequest() }) {
                        Text(stringResource(Res.string.label_no))
                    }
                    TextButton({ onConfirmation();onDismissRequest() }) {
                        Text(stringResource(Res.string.label_yes))
                    }
                }
            }
        }
    }
}
