package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowSnackbarOnFailure
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen.Companion.isTwoPanes
import org.jetbrains.compose.resources.StringResource
import kotlin.jvm.Transient

class ResultScreen(
    override val key: String
) : NavigationScreen() {

    companion object {
        @Composable
        fun rememberScreenModel(key: String): ResultScreenModel {
            val appState = LocalAppState.current
            val model = appState.navigator.rememberNavigatorScreenModel(key) {
                ResultScreenModel()
            }
            return model
        }
    }

    @delegate:Transient
    override var title: StringResource? by mutableStateOf(null)

    @Composable
    private fun rememberScreenModel(): ResultScreenModel {
        return rememberScreenModel(key)
    }

    @Composable
    override fun Content() {
        val appState = LocalAppState.current
        val model = rememberScreenModel()
        LaunchedEffect(model.title) {
            title = model.title
        }

        // 如果是双栏，直接弹出
        // 调用onResultMove将result移动回去，从而能在上层展示
        LaunchedEffect(appState.windowSizeClass) {
            if (isTwoPanes(appState.windowSizeClass)) {
                appState.navigator.pop()
                model.resultHolder?.moveResult()
            }
        }

        Calculation(
            model,
            {
                model.resultHolder?.result?.await()
            },
            onFailure = {
                PopAndShowSnackbarOnFailure(it)
            }
        ) { result ->
            result?.let { model.resultContent(it) }
        }
    }
}