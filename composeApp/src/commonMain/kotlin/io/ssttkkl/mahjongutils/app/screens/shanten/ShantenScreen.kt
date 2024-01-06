package io.ssttkkl.mahjongutils.app.screens.shanten

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
import io.ssttkkl.mahjongutils.app.components.basic.RatioGroups
import io.ssttkkl.mahjongutils.app.components.basic.RatioOption
import io.ssttkkl.mahjongutils.app.components.panel.Caption
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.tile.AutoSingleLineTiles
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenCalcResult
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenMode
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedFormatting
import mahjongutils.shanten.ShantenWithoutGot
import mahjongutils.shanten.asWithGot
import mahjongutils.shanten.asWithoutGot

@Composable
private fun ShantenModeRatioGroups(
    value: ShantenMode,
    onValueChanged: (ShantenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val radioOptions = listOf(
        RatioOption(
            ShantenMode.Union,
            stringResource(MR.strings.label_union_shanten),
            stringResource(MR.strings.text_union_shanten_desc)
        ),
        RatioOption(
            ShantenMode.Regular,
            stringResource(MR.strings.label_regular_shanten),
            stringResource(MR.strings.text_regular_shanten_desc)
        ),
    )

    RatioGroups(radioOptions, value, onValueChanged, modifier)
}

object ShantenScreen :
    FormAndResultScreen<ShantenScreenModel, ShantenArgs, ShantenCalcResult>() {
    override val title
        get() = MR.strings.title_shanten

    override val resultTitle
        get() = MR.strings.title_shanten_result

    @Composable
    override fun getScreenModel(): ShantenScreenModel {
        return rememberScreenModel { ShantenScreenModel() }
    }

    @Composable
    override fun FormContent(
        appState: AppState,
        model: ShantenScreenModel,
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
                            label = stringResource(MR.strings.label_tiles_in_hand)
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    { Text(stringResource(MR.strings.label_shanten_mode)) },
                    noContentPadding = true
                ) {
                    ShantenModeRatioGroups(
                        model.shantenMode,
                        { model.shantenMode = it }
                    )
                }

                VerticalSpacerBetweenPanels()

                Button(
                    modifier = Modifier.windowHorizontalMargin(),
                    content = { Text(stringResource(MR.strings.label_calc)) },
                    onClick = {
                        model.onSubmit()
                    }
                )

                VerticalSpacerBetweenPanels()
            }
        }
    }

    @Composable
    override fun ResultContent(
        appState: AppState,
        result: ShantenCalcResult,
        modifier: Modifier
    ) {
        if (result.result.shantenInfo is ShantenWithoutGot) {
            ShantenResultContent(result.args, result.result.shantenInfo.asWithoutGot)
        } else {
            ShantenResultContent(result.args, result.result.shantenInfo.asWithGot)
        }
    }

    @Composable
    override fun HistoryItem(item: History<ShantenArgs>, model: ShantenScreenModel) {
        Column {
            AutoSingleLineTiles(item.args.tiles)

            Spacer(Modifier.height(8.dp))

            Caption(title = {
                Text(
                    when (item.args.mode) {
                        ShantenMode.Union -> stringResource(MR.strings.label_union_shanten)
                        ShantenMode.Regular -> stringResource(MR.strings.label_regular_shanten)
                    }
                )
            })

            Spacer(Modifier.height(16.dp))

            Text(
                item.createTime.localizedFormatting(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    override fun onClickHistoryItem(
        item: History<ShantenArgs>,
        model: ShantenScreenModel,
        appState: AppState
    ) {
        model.tiles = item.args.tiles
        model.shantenMode = item.args.mode

        model.postCheck()
    }
}
