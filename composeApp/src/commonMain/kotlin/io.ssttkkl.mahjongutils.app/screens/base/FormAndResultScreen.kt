package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.Deferred

abstract class FormAndResultScreen<M : ScreenModel, RES> : Screen {
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
    abstract fun resultState(model: M): State<Deferred<RES>?>

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
        val result by resultState(model)

        LaunchedEffect(appState, result) {
            result?.let { result ->
                appState.navigator.push(ResultScreen(resultTitle, result) {
                    ResultContent(appState, it, Modifier)
                })
            }
        }

        FormContent(appState, model, Modifier)
    }

    @Composable
    private fun TwoPaneContent(appState: AppState, model: M) {
        val result by resultState(model)

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