package io.ssttkkl.mahjongutils.app.screens.shanten

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
import io.ssttkkl.mahjongutils.app.components.tile.AutoSingleLineTiles
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenCalcResult
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenMode
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.localizedFormatting
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_calc
import mahjongutils.composeapp.generated.resources.label_regular_shanten
import mahjongutils.composeapp.generated.resources.label_shanten_mode
import mahjongutils.composeapp.generated.resources.label_union_shanten
import mahjongutils.composeapp.generated.resources.title_shanten
import mahjongutils.composeapp.generated.resources.title_shanten_result
import org.jetbrains.compose.resources.stringResource

object ShantenScreen :
    FormAndResultScreen<ShantenScreenModel, ShantenArgs, ShantenCalcResult>() {
    override val title
        get() = Res.string.title_shanten

    override val resultTitle
        get() = Res.string.title_shanten_result

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
        val components = remember(model.form) { ShantenFormComponents(model.form) }
        val verticalScrollState = rememberScrollState()

        with(Spacing.current) {
            ScrollBox(verticalScrollState = verticalScrollState, modifier = modifier) {
                Column(
                    Modifier.verticalScroll(verticalScrollState)
                ) {
                    VerticalSpacerBetweenPanels()

                    TopPanel {
                        components.Tiles()
                    }

                    VerticalSpacerBetweenPanels()

                    TopPanel(
                        { Text(stringResource(Res.string.label_shanten_mode)) },
                        noContentPadding = true
                    ) {
                        components.ShantenMode()
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
        result: ShantenCalcResult,
        modifier: Modifier
    ) {
        val handler = getChangeArgsByResultContentHandler()
        ShantenResultContent(result.args, result.result.shantenInfo) {
            handler(it)
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
                        ShantenMode.Union -> stringResource(Res.string.label_union_shanten)
                        ShantenMode.Regular -> stringResource(Res.string.label_regular_shanten)
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
        model.fillFormWithArgs(item.args)
    }
}
