package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.base.rememberWindowSizeClass
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreen
import io.ssttkkl.mahjongutils.app.models.base.History
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Stable
abstract class FormAndResultScreen<M : FormAndResultScreenModel<ARG, RES>, ARG, RES> :
    UrlNavigationScreen<M>() {
    companion object {
        fun isTwoPanes(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                    && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
        }
    }

    override val navigationTitle: String
        @Composable
        get() = stringResource(formTitle)

    abstract val formTitle: StringResource

    abstract val resultTitle: StringResource

    @Composable
    abstract fun FormContent(appState: AppState, model: M)

    // 加上protected，ios link报错？？？
    @Composable
    /*protected*/ abstract fun HistoryItem(item: History<ARG>, model: M)

    protected abstract fun onClickHistoryItem(item: History<ARG>, model: M, appState: AppState)

    @Composable
    abstract fun ResultContent(appState: AppState, result: RES)

    @Composable
    override fun ScreenContent() {
        val appState = LocalAppState.current
        val windowSizeClass = rememberWindowSizeClass()
        val showTwoPanes by rememberUpdatedState(isTwoPanes(windowSizeClass))
        val initialScreen = remember(showTwoPanes) {
            if (showTwoPanes) {
                NestedFormAndResultScreen<ARG, RES>(key)
            } else {
                NestedFormScreen<ARG, RES>(key)
            }
        }

        val model = rememberScreenModel()

        NestedNavigator(initialScreen) { nestedNavigator ->
            CurrentScreen()

            val (_, resultModel) = rememberFormAndResultScreenModel(
                appState,
                model,
                nestedNavigator
            )

            val curNavigator by rememberUpdatedState(nestedNavigator)
            LaunchedEffect(resultModel) {
                model.onResult = { result ->
                    resultModel.result = result
                    if (resultModel.result != null && !showTwoPanes) {
                        curNavigator.push(NestedResultScreen<ARG, RES>(key))
                    }
                }
            }
        }
    }

    @Composable
    private fun rememberFormAndResultScreenModel(
        appState: AppState,
        parentScreenModel: M,
        nestedNavigator: Navigator,
    ): Pair<NestedFormScreenModel<ARG, RES>, NestedResultScreenModel<ARG, RES>> {
        val formScreenModel = NestedFormScreen.rememberScreenModel<ARG, RES>(key, nestedNavigator)
        fillFormScreenModel(formScreenModel, appState, parentScreenModel)

        val resultScreenModel =
            NestedResultScreen.rememberScreenModel<ARG, RES>(key, nestedNavigator)
        fillResultScreenModel(resultScreenModel, appState, parentScreenModel)

        return Pair(formScreenModel, resultScreenModel)
    }

    @Composable
    private fun fillFormScreenModel(
        model: NestedFormScreenModel<ARG, RES>,
        appState: AppState,
        parentScreenModel: M
    ) {
        LaunchedEffect(model, formTitle) {
            model.title = formTitle
        }
        LaunchedEffect(model, parentScreenModel) {
            model.parentScreenModel = parentScreenModel
            model.historyItem = {
                HistoryItem(it, parentScreenModel)
            }
        }
        LaunchedEffect(model, appState, parentScreenModel) {
            model.formContent = {
                FormContent(appState, parentScreenModel)
            }
            model.onClickHistoryItem = {
                onClickHistoryItem(it, parentScreenModel, appState)
            }
        }
    }

    @Composable
    private fun fillResultScreenModel(
        model: NestedResultScreenModel<ARG, RES>,
        appState: AppState,
        parentScreenModel: M
    ) {
        LaunchedEffect(model, resultTitle) {
            model.title = resultTitle
        }
        LaunchedEffect(model, parentScreenModel) {
            model.parentScreenModel = parentScreenModel
        }
        LaunchedEffect(model, appState) {
            model.resultContent = { res ->
                ResultContent(appState, res)
            }
        }
    }

    @Composable
    fun getChangeArgsByResultContentHandler(): (args: ARG) -> Unit {
        val curModel by rememberUpdatedState(rememberScreenModel())
        val curNavigator by rememberUpdatedState(screenState.nestedNavigator)
        return { args ->
            curNavigator?.popUntilRoot()
            curModel.fillFormWithArgs(args)
            curModel.onSubmit()
        }
    }

    @Composable
    override fun rememberScreenParams(): Map<String, String> {
        val model = rememberScreenModel()
        return model.extractToMap()
    }

    override fun applyScreenParams(model: M, params: Map<String, String>) {
        model.applyFromMap(params)
    }
}