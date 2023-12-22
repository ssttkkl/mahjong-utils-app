package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.components.capture.Capturable
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen.Companion.isTwoPanes
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
    override fun RowScope.TopBarActions(appState: AppState) {
        val model = rememberScreenModel()

        if (!model.calculating) {
            IconButton(onClick = {
                model.onCapture()
            }) {
                Icon(Icons.Outlined.Share, stringResource(MR.strings.label_share))
            }
        }
    }

    @Composable
    override fun Content() {
        super.Content()

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
                PopAndShowMessageOnFailure(it)
            }
        ) { result ->
            Capturable(model.captureState) {
                result?.let { model.resultContent(it) }
            }
        }
    }
}