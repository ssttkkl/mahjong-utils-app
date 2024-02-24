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
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.rememberScreenModel
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.panel.TopPanel
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.utils.Spacing
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_calc
import mahjongutils.composeapp.generated.resources.label_han
import mahjongutils.composeapp.generated.resources.label_hora_options
import mahjongutils.composeapp.generated.resources.label_hu
import mahjongutils.composeapp.generated.resources.title_hanhu
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object HanhuScreen : NavigationScreen() {
    override val title: StringResource
        get() = Res.string.title_hanhu

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
                            label = { Text(stringResource(Res.string.label_han)) },
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
                            label = { Text(stringResource(Res.string.label_hu)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = isError
                        )
                    }
                }

                VerticalSpacerBetweenPanels()

                var optionsDialogVisible by rememberSaveable { mutableStateOf(false) }
                if (optionsDialogVisible) {
                    HanhuOptionsDialog(
                        model.hanhuOptions,
                        onChangeOptions = {
                            model.hanhuOptions = it
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
                val verticalScrollState = rememberScrollState()

                ScrollBox(verticalScrollState = verticalScrollState) {
                    Column(
                        Modifier.verticalScroll(verticalScrollState)
                    ) {
                        VerticalSpacerBetweenPanels()
                        Form()
                        VerticalSpacerBetweenPanels()
                        Result()
                        VerticalSpacerBetweenPanels()
                    }
                }
            } else {
                Row {
                    val formScrollState = rememberScrollState()
                    val resultScrollState = rememberScrollState()

                    ScrollBox(verticalScrollState = formScrollState, modifier = Modifier.weight(2f)) {
                        Column(
                            Modifier.verticalScroll(formScrollState)
                        ) {
                            VerticalSpacerBetweenPanels()
                            Form()
                            VerticalSpacerBetweenPanels()
                        }
                    }

                    ScrollBox(verticalScrollState = resultScrollState, modifier = Modifier.weight(3f)) {
                        Column(
                            Modifier.verticalScroll(resultScrollState)
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
}