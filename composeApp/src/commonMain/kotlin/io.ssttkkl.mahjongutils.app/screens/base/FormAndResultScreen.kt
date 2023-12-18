package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.utils.Spacing

abstract class FormAndResultScreen<M : ScreenModel, ARGS : Any> : Screen {
    @Composable
    abstract fun produceScreenModel(): M

    @Composable
    abstract fun latestEmittedArgs(model: M): ARGS?

    abstract fun produceResultScreen(args: ARGS): Screen

    @Composable
    abstract fun FormContent(appState: AppState, model: M, modifier: Modifier)

    @Composable
    override fun Content() {
        val appState = LocalAppState.current
        val model = produceScreenModel()

        if (appState.windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            || appState.windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
        ) {
            OnePaneContent(appState, model)
        } else {
            TwoPaneContent(appState, model)
        }
    }

    @Composable
    private fun OnePaneContent(appState: AppState, model: M) {
        val args = latestEmittedArgs(model)

        LaunchedEffect(appState, args) {
            args?.let { args ->
                appState.navigator.push(produceResultScreen(args))
            }
        }

        FormContent(appState, model, Modifier)
    }

    @Composable
    private fun TwoPaneContent(appState: AppState, model: M) {
        val args = latestEmittedArgs(model)

        with(Spacing.current) {
            Row {
                FormContent(appState, model, Modifier.weight(2f))

                Spacer(Modifier.width(panesHorizontalSpacing))

                Box(Modifier.weight(3f)) {
                    args?.let { args ->
                        produceResultScreen(args).Content()
                    }
                }
            }
        }
    }
}