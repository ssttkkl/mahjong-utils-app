package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen.Companion.isTwoPanes
import kotlinx.coroutines.Deferred

data class ResultScreen<RES>(
    override val title: StringResource,
    val result: Deferred<RES>,
    val onResultMove: (Deferred<RES>) -> Unit,
    val resultContent: @Composable (RES) -> Unit
) : NavigationScreen {

    @Composable
    override fun Content() {
        val appState = LocalAppState.current

        // 如果是双栏，直接弹出
        // 调用onResultMove将result移动回去，从而能在上层展示
        LaunchedEffect(appState.windowSizeClass) {
            if (isTwoPanes(appState.windowSizeClass)) {
                appState.navigator.pop()
                onResultMove(result)
            }
        }

        Calculation(
            result,
            {
                result.await()
            },
            onFailure = {
                PopAndShowMessageOnFailure(it)
            }
        ) {
            resultContent(it)
        }
    }
}