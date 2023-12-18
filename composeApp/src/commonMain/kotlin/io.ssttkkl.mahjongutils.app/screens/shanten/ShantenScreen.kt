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
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.navigator.NavigationScreen
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.ratio.RatioGroups
import io.ssttkkl.mahjongutils.app.components.ratio.RatioOption
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing

@Composable
private fun ShantenModeRatioGroups(
    value: ShantenMode,
    onValueChanged: (ShantenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val radioOptions = listOf(
        RatioOption(
            ShantenMode.Union,
            Res.string.label_union_shanten,
            Res.string.text_union_shanten_desc
        ),
        RatioOption(
            ShantenMode.Regular,
            Res.string.label_regular_shanten,
            Res.string.text_regular_shanten_desc
        ),
    )

    RatioGroups(radioOptions, value, onValueChanged, modifier)
}

object ShantenScreen : FormAndResultScreen<ShantenScreenModel, ShantenArgs>(), NavigationScreen {
    override val title: String
        get() = Res.string.title_shanten

    @Composable
    override fun produceScreenModel(): ShantenScreenModel {
        return rememberScreenModel { ShantenScreenModel() }
    }

    @Composable
    override fun latestEmittedArgs(model: ShantenScreenModel): ShantenArgs? {
        val args by model.produceArgs.collectAsState(null)
        return args
    }

    override fun produceResultScreen(args: ShantenArgs): Screen {
        return ShantenResultScreen(args)
    }

    @Composable
    override fun FormContent(
        appState: AppState,
        model: ShantenScreenModel,
        modifier: Modifier
    ) {
        val snackbarHostState = appState.snackbarHostState

        val tilesState by model.tiles.collectAsState()
        val shantenModeState by model.shantenMode.collectAsState()

        with(Spacing.current) {
            Column(
                modifier.verticalScroll(rememberScrollState())
            ) {
                VerticalSpacerBetweenPanels()

                TopPanel(Res.string.label_tiles_in_hand) {
                    TileField(
                        value = tilesState,
                        onValueChange = { model.tiles.value = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    Res.string.label_shanten_mode,
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
                    content = { Text(Res.string.text_calc) },
                    onClick = {
                        model.onSubmit(snackbarHostState)
                    }
                )

                VerticalSpacerBetweenPanels()
            }
        }
    }
}
