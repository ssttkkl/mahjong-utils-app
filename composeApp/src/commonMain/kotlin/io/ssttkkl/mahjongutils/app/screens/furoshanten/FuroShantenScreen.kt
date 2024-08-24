package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.panel.Caption
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedFormatting
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_allow_chi
import mahjongutils.composeapp.generated.resources.label_calc
import mahjongutils.composeapp.generated.resources.label_other_options
import mahjongutils.composeapp.generated.resources.text_false_symbol
import mahjongutils.composeapp.generated.resources.title_furo_shanten
import mahjongutils.composeapp.generated.resources.title_furo_shanten_result
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


object FuroShantenScreen :
    FormAndResultScreen<FuroShantenScreenModel, FuroChanceShantenArgs, FuroChanceShantenCalcResult>() {

    override val path: String
        get() = "furoShanten"

    override val formTitle: StringResource
        get() = Res.string.title_furo_shanten

    override val resultTitle
        get() = Res.string.title_furo_shanten_result

    @Composable
    override fun rememberScreenModel(): FuroShantenScreenModel {
        return rememberScreenModel { FuroShantenScreenModel() }
    }

    @Composable
    override fun FormContent(
        appState: AppState,
        model: FuroShantenScreenModel
    ) {
        val components = remember(model.form) { FuroShantenComponents(model.form) }
        val verticalScrollState = rememberScrollState()

        with(Spacing.current) {
            ScrollBox(verticalScrollState = verticalScrollState) {
                Column(
                    Modifier.verticalScroll(verticalScrollState)
                ) {
                    VerticalSpacerBetweenPanels()

                    TopPanel {
                        components.Tiles()
                    }

                    VerticalSpacerBetweenPanels()
                    TopPanel {
                        components.ChanceTile()
                    }

                    VerticalSpacerBetweenPanels()

                    TopPanel(
                        { Text(stringResource(Res.string.label_other_options)) },
                        noContentPadding = true
                    ) {
                        components.AllowChi()
                    }

                    VerticalSpacerBetweenPanels()

                    Button(
                        modifier = Modifier.windowHorizontalMargin(),
                        content = { Text(stringResource(Res.string.label_calc)) },
                        onClick = {
                            model.onSubmit()
                        }
                    )

                    VerticalSpacerBetweenPanels()
                }
            }
        }
    }

    @Composable
    override fun ResultContent(
        appState: AppState,
        result: FuroChanceShantenCalcResult
    ) {
        val handler = getChangeArgsByResultContentHandler()
        FuroShantenResultContent(result.args, result.result.shantenInfo) {
            handler(it)
        }
    }

    @Composable
    override fun HistoryItem(item: History<FuroChanceShantenArgs>, model: FuroShantenScreenModel) {
        Column {
            FuroShantenTiles(item.args.tiles, item.args.chanceTile)

            if (!item.args.allowChi) {
                Spacer(Modifier.height(8.dp))

                Caption(
                    title = { Text(stringResource(Res.string.label_allow_chi)) },
                    content = { Text(stringResource(Res.string.text_false_symbol)) }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                item.createTime.localizedFormatting(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    override fun onClickHistoryItem(
        item: History<FuroChanceShantenArgs>,
        model: FuroShantenScreenModel,
        appState: AppState
    ) {
        model.fillFormWithArgs(item.args)
    }
}

