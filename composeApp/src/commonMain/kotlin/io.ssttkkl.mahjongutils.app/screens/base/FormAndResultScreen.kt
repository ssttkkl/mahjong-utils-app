package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class FormAndResultScreen<M : ResultScreenModel<RES>, RES> : NavigationScreen {
    companion object {
        fun isTwoPanes(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                    && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
        }
    }

    abstract val resultTitle: String

    @Composable
    abstract fun getScreenModel(): M

    @Composable
    abstract fun FormContent(appState: AppState, model: M, modifier: Modifier)

    @Composable
    abstract fun ResultContent(appState: AppState, result: RES, modifier: Modifier)

    @Composable
    override fun Content() {
        val appState = LocalAppState.current
        val model = getScreenModel()

        if (isTwoPanes(appState.windowSizeClass)) {
            TwoPaneContent(appState, model)
        } else {
            OnePaneContent(appState, model)
        }
    }

    @Composable
    private fun OnePaneContent(appState: AppState, model: M) {
        val coroutineScope = rememberCoroutineScope()

        DisposableEffect(appState, model, coroutineScope) {
            val job = coroutineScope.launch {
                model.result.collectLatest { result ->
                    result?.let { result ->
                        // 将result移动至ResultScreen
                        // 保证从ResultScreen返回后不会重复跳转
                        // 除非调用了onResultMove将result再次移动回来（发生在旋转后由单栏变成双栏的场合）
                        appState.navigator.push(
                            ResultScreen(
                                title = resultTitle,
                                result = result,
                                onResultMove = { model.result.value = it }) {
                                ResultContent(appState, it, Modifier)
                            })
                        model.result.value = null
                    }
                }
            }

            onDispose {
                job.cancel()
            }
        }

        FormContent(appState, model, Modifier)
    }

    @Composable
    private fun TwoPaneContent(appState: AppState, model: M) {
        val result by model.result.collectAsState()

        with(Spacing.current) {
            Row {
                FormContent(appState, model, Modifier.weight(2f))
                Spacer(Modifier.width(panesHorizontalSpacing))
                Box(Modifier.weight(3f)) {
                    result?.let { result ->
                        Calculation(
                            result,
                            {
                                result.await()
                            },
                            onFailure = {
                                PopAndShowMessageOnFailure(it)
                            }
                        ) {
                            ResultContent(appState, it, Modifier)
                        }
                    }
                }
            }
        }
    }
}