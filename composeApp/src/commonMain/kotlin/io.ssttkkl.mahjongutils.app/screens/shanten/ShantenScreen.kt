package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.ratio.RatioGroups
import io.ssttkkl.mahjongutils.app.components.ratio.RatioOption
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.launch
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
    FormAndResultScreen<ShantenScreenModel, ShantenCalcResult>() {
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

        val tilesState by model.tiles.collectAsState()
        val shantenModeState by model.shantenMode.collectAsState()

        val tilesErrMsg by model.tilesErrMsg.collectAsState()

        with(Spacing.current) {
            Column(
                modifier.verticalScroll(rememberScrollState())
            ) {
                VerticalSpacerBetweenPanels()

                TopPanel(stringResource(MR.strings.label_tiles_in_hand)) {
                    ValidationField(tilesErrMsg?.let { stringResource(it) }) { isError ->
                        TileField(
                            value = tilesState,
                            onValueChange = { model.tiles.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isError
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    stringResource(MR.strings.label_shanten_mode),
                    noPaddingContent = true
                ) {
                    ShantenModeRatioGroups(
                        shantenModeState,
                        { model.shantenMode.value = it }
                    )
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
        result: ShantenCalcResult,
        modifier: Modifier
    ) {
        if (result.result.shantenInfo is ShantenWithoutGot) {
            ShantenResultContent(result.args, result.result.shantenInfo.asWithoutGot)
        } else {
            ShantenResultContent(result.args, result.result.shantenInfo.asWithGot)
        }
    }
}
