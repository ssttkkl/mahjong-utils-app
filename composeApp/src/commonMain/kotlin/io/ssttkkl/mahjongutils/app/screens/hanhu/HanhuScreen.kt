package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.rememberScreenModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing

object HanhuScreen : NavigationScreen() {
    override val title: StringResource
        get() = MR.strings.title_hanhu

    @Composable
    override fun Content() {
        val model = rememberScreenModel { HanhuScreenModel() }

        with(Spacing.current) {
            @Composable
            fun Form() {
                TopPanel {
                    ValidationField(model.hanErr, Modifier.fillMaxWidth()) { isError ->
                        OutlinedTextField(
                            model.han, { model.han = it }, Modifier.fillMaxWidth(),
                            label = { Text(stringResource(MR.strings.label_han)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = isError
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                TopPanel {
                    ValidationField(model.huErr, Modifier.fillMaxWidth()) { isError ->
                        OutlinedTextField(
                            model.hu, { model.hu = it }, Modifier.fillMaxWidth(),
                            label = { Text(stringResource(MR.strings.label_hu)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = isError
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                Button(
                    modifier = Modifier.windowHorizontalMargin(),
                    content = { Text(stringResource(MR.strings.label_calc)) },
                    onClick = {
                        model.onSubmit()
                    }
                )
            }

            @Composable
            fun Result() {
                Calculation(
                    model.result,
                    { Triple(it, it?.parentPoint?.await(), it?.childPoint?.await()) },
                    onCalculating = {}
                ) { (result, parentPoint, childPoint) ->
                    if (result != null) {
                        PointPanel(
                            result.han,
                            result.hu,
                            false,
                            parentPoint,
                            childPoint
                        )

                        VerticalSpacerBetweenPanels()
                    }
                }
            }

            if (LocalAppState.current.windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {
                    VerticalSpacerBetweenPanels()
                    Form()
                    VerticalSpacerBetweenPanels()
                    Result()
                    VerticalSpacerBetweenPanels()
                }
            } else {
                Row {
                    Column(
                        Modifier.weight(2f).verticalScroll(rememberScrollState())
                    ) {
                        VerticalSpacerBetweenPanels()
                        Form()
                        VerticalSpacerBetweenPanels()
                    }

                    Column(
                        Modifier.weight(3f).verticalScroll(rememberScrollState())
                    ) {
                        VerticalSpacerBetweenPanels()
                        Result()
                        VerticalSpacerBetweenPanels()
                    }
                }
            }
        }
    }
}