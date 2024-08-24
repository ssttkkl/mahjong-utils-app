package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreen
import io.ssttkkl.mahjongutils.app.models.base.History
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

abstract class FormAndResultScreen<M : FormAndResultScreenModel<ARG, RES>, ARG, RES> :
    UrlNavigationScreen<M>() {
    companion object {
        fun isTwoPanes(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                    && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
        }

        @Composable
        fun isTwoPanes(): Boolean {
            return isTwoPanes(LocalAppState.current.windowSizeClass)
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
        val showTwoPanes = isTwoPanes(appState.windowSizeClass)
        val initialScreen = remember {
            if (showTwoPanes) {
                NestedFormAndResultScreen<ARG, RES>(key)
            } else {
                NestedFormScreen<ARG, RES>(key)
            }
        }
        NestedNavigator(initialScreen) { nestedNavigator ->
            val model = getScreenModel()
            initFormAndResultScreenModel(
                nestedNavigator,
                appState,
                model
            )

            LaunchedEffect(model.result) {
                if (model.result != null && !showTwoPanes && nestedNavigator.items.none { it is NestedResultShared<*, *> && it.key == key }) {
                    nestedNavigator.push(NestedResultScreen<ARG, RES>(key))
                }
            }

            CurrentScreen()
        }
    }

    @Composable
    private fun initFormAndResultScreenModel(
        navigator: Navigator,
        appState: AppState,
        parentScreenModel: M
    ): Pair<NestedFormScreenModel<ARG, RES>, NestedResultScreenModel<ARG, RES>> {
        val formScreenModel = NestedFormScreen.rememberScreenModel<ARG, RES>(key, navigator)
        fillFormScreenModel(formScreenModel, appState, parentScreenModel)

        val resultScreenModel = NestedResultScreen.rememberScreenModel<ARG, RES>(key, navigator)
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
        val curModel by rememberUpdatedState(getScreenModel())
        val curNavigator by rememberUpdatedState(screenState.nestedNavigator)
        return { args ->
            curNavigator?.popUntilRoot()
            curModel.fillFormWithArgs(args)
            curModel.onSubmit()
        }
    }
}