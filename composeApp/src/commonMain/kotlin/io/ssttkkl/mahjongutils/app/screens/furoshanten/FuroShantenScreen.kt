package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.basic.SwitchItem
import io.ssttkkl.mahjongutils.app.components.panel.Caption
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedFormatting
import kotlinx.coroutines.launch


object FuroShantenScreen :
    FormAndResultScreen<FuroShantenScreenModel, FuroChanceShantenArgs, FuroChanceShantenCalcResult>() {

    override val title
        get() = MR.strings.title_furo_shanten

    override val resultTitle
        get() = MR.strings.title_furo_shanten_result

    @Composable
    override fun getScreenModel(): FuroShantenScreenModel {
        return rememberScreenModel { FuroShantenScreenModel() }
    }

    @Composable
    override fun FormContent(
        appState: AppState,
        model: FuroShantenScreenModel,
        modifier: Modifier
    ) {
        val coroutineScope = rememberCoroutineScope()

        with(Spacing.current) {
            Column(
                modifier.verticalScroll(rememberScrollState())
            ) {
                VerticalSpacerBetweenPanels()

                TopPanel {
                    ValidationField(model.tilesErrMsg) { isError ->
                        TileField(
                            value = model.tiles,
                            onValueChange = { model.tiles = it },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isError,
                            label = { Text(stringResource(MR.strings.label_tiles_in_hand)) }
                        )
                    }
                }

                VerticalSpacerBetweenPanels()
                TopPanel {
                    ValidationField(model.chanceTileErrMsg) { isError ->
                        TileField(
                            value = model.chanceTile?.let { listOf(it) } ?: emptyList(),
                            onValueChange = { model.chanceTile = it.firstOrNull() },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isError,
                            label = { Text(stringResource(MR.strings.label_tile_discarded_by_other)) }
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    { Text(stringResource(MR.strings.label_other_options)) },
                    noContentPadding = true
                ) {
                    SwitchItem(
                        model.allowChi,
                        { model.allowChi = !model.allowChi },
                        stringResource(MR.strings.label_allow_chi)
                    )
                }

                VerticalSpacerBetweenPanels()

                Button(
                    modifier = Modifier.windowHorizontalMargin(),
                    content = { Text(stringResource(MR.strings.label_calc)) },
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
        result: FuroChanceShantenCalcResult,
        modifier: Modifier
    ) {
        FuroShantenResultContent(result.args, result.result.shantenInfo)
    }

    @Composable
    override fun HistoryItem(item: History<FuroChanceShantenArgs>, model: FuroShantenScreenModel) {
        Column {
            FuroShantenTiles(item.args.tiles, item.args.chanceTile)

            if (!item.args.allowChi) {
                Spacer(Modifier.height(8.dp))

                Caption(
                    title = { Text(stringResource(MR.strings.label_allow_chi)) },
                    content = { Text(stringResource(MR.strings.text_no_symbol)) }
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
        model: FuroShantenScreenModel
    ) {
        model.tiles = item.args.tiles
        model.chanceTile = item.args.chanceTile
        model.allowChi = item.args.allowChi

        model.postCheck()
    }
}

