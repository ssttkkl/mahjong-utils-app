package io.ssttkkl.mahjongutils.app.screens.furoshanten

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
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.switch.SwitchItem
import io.ssttkkl.mahjongutils.app.components.tilefield.TileField
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing


object FuroShantenScreen : FormAndResultScreen<FuroShantenScreenModel, FuroChanceShantenArgs>(),
    NavigationScreen {
    override val title: String
        get() = Res.string.title_furo_shanten

    @Composable
    override fun produceScreenModel(): FuroShantenScreenModel {
        return rememberScreenModel { FuroShantenScreenModel() }
    }

    @Composable
    override fun latestEmittedArgs(model: FuroShantenScreenModel): FuroChanceShantenArgs? {
        val args by model.produceArgs.collectAsState(null)
        return args
    }

    override fun produceResultScreen(args: FuroChanceShantenArgs): Screen {
        return FuroShantenResultScreen(args)
    }

    @Composable
    override fun FormContent(
        appState: AppState,
        model: FuroShantenScreenModel,
        modifier: Modifier
    ) {
        val tilesState by model.tiles.collectAsState()
        val chanceTileState by model.chanceTile.collectAsState()
        val allowChiState by model.allowChi.collectAsState()

        with(Spacing.current) {
            Column(
                Modifier.verticalScroll(rememberScrollState())
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

                TopPanel(Res.string.label_tile_discarded_by_other) {
                    TileField(
                        value = chanceTileState?.let { listOf(it) } ?: emptyList(),
                        onValueChange = { model.chanceTile.value = it.firstOrNull() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                VerticalSpacerBetweenPanels()

                TopPanel(
                    Res.string.label_other_options,
                    noPaddingContent = true
                ) {
                    SwitchItem(
                        allowChiState,
                        { model.allowChi.value = !allowChiState },
                        Res.string.label_allow_chi
                    )
                }

                VerticalSpacerBetweenPanels()

                Button(
                    modifier = Modifier.windowHorizontalMargin(),
                    content = { Text(Res.string.text_calc) },
                    onClick = {
                        model.onSubmit(appState.snackbarHostState)
                    }
                )

                VerticalSpacerBetweenPanels()
            }
        }
    }
}

