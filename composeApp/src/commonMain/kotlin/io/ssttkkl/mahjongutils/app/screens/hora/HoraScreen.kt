package io.ssttkkl.mahjongutils.app.screens.hora

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppDialogState
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.panel.Caption
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.models.hora.HoraCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedFormatting
import io.ssttkkl.mahjongutils.app.utils.localizedName
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_calc
import mahjongutils.composeapp.generated.resources.label_dora_count
import mahjongutils.composeapp.generated.resources.label_extra_yaku
import mahjongutils.composeapp.generated.resources.label_hora_options
import mahjongutils.composeapp.generated.resources.label_no
import mahjongutils.composeapp.generated.resources.label_other_information
import mahjongutils.composeapp.generated.resources.label_ron
import mahjongutils.composeapp.generated.resources.label_round_wind
import mahjongutils.composeapp.generated.resources.label_self_wind
import mahjongutils.composeapp.generated.resources.label_tsumo
import mahjongutils.composeapp.generated.resources.label_yes
import mahjongutils.composeapp.generated.resources.text_comma
import mahjongutils.composeapp.generated.resources.text_overwrite_hora_options_hint
import mahjongutils.composeapp.generated.resources.title_hora
import mahjongutils.composeapp.generated.resources.title_hora_result
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


object HoraScreen :
    FormAndResultScreen<HoraScreenModel, HoraArgs, HoraCalcResult>() {

    override val path: String
        get() = "hora"

    override val formTitle: StringResource
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
        model: HoraScreenModel
    ) {
        val components = remember(model.form) { HoraFormComponents(model.form) }
        val verticalScrollState = rememberScrollState()

        with(Spacing.current) {
            ScrollBox(verticalScrollState = verticalScrollState) {
                Column(
                    Modifier.verticalScroll(verticalScrollState)
                ) {
                    VerticalSpacerBetweenPanels()

                    // 手牌
                    TopPanel {
                        components.Tiles()
                    }

                    VerticalSpacerBetweenPanels()

                    TopPanel {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 所和的牌
                            components.Agari(Modifier.weight(1f))

                            // 自摸/荣和
                            components.Tsumo()
                        }
                    }

                    VerticalSpacerBetweenPanels()

                    // 副露
                    components.Furo()

                    VerticalSpacerBetweenPanels()

                    TopPanel(
                        { Text(stringResource(Res.string.label_other_information)) },
                        noContentPadding = true
                    ) {
                        Row {
                            // 自风
                            components.SelfWind(Modifier.weight(1f))
                            // 场风
                            components.RoundWind(Modifier.weight(1f))
                        }

                        VerticalSpacerBetweenPanels()

                        Row {
                            // dora
                            TopPanel(modifier = Modifier.weight(1f)) {
                                components.Dora()
                            }

                            // 额外役种
                            TopPanel(modifier = Modifier.weight(1f)) {
                                components.ExtraYaku()
                            }
                        }
                    }

                    VerticalSpacerBetweenPanels()

                    // 选项对话框
                    var optionsDialogVisible by rememberSaveable { mutableStateOf(false) }
                    if (optionsDialogVisible) {
                        components.HoraOptionsDialog {
                            optionsDialogVisible = false
                        }
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
        result: HoraCalcResult
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
        val args = item.args
        if (model.form.horaOptions != args.options) {
            appState.appDialogState = AppDialogState { onDismissRequest ->
                OverwriteHoraOptionsAlertDialog(onDismissRequest) { yes ->
                    if (yes) {
                        model.fillFormWithArgs(args)
                    } else {
                        model.fillFormWithArgs(args.copy(options = model.form.horaOptions))
                    }
                }
            }.also {
                it.visible = true
            }
        } else {
            model.fillFormWithArgs(args)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverwriteHoraOptionsAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (yes: Boolean) -> Unit
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
                    TextButton({ onConfirmation(false);onDismissRequest() }) {
                        Text(stringResource(Res.string.label_no))
                    }
                    TextButton({ onConfirmation(true);onDismissRequest() }) {
                        Text(stringResource(Res.string.label_yes))
                    }
                }
            }
        }
    }
}
