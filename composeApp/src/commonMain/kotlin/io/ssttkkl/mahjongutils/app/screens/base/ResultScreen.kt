package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.components.capture.Capturable
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreen.Companion.isTwoPanes
import kotlinx.coroutines.Deferred

data class ResultScreen<RES>(
    override val title: StringResource,
    val result: Deferred<RES>,
    val onResultMove: (Deferred<RES>) -> Unit,
    val resultContent: @Composable (RES) -> Unit
) : NavigationScreen() {

    @Composable
    override fun RowScope.TopBarActions(appState: AppState) {
        val captureModel = rememberScreenModel { ResultCaptureScreenModel() }

        if (!captureModel.calculating) {
            IconButton(onClick = {
                captureModel.onCapture()
            }) {
                Icon(Icons.Outlined.Share, stringResource(MR.strings.label_share))
            }
        }
    }

    @Composable
    override fun Content() {
        super.Content()

        val appState = LocalAppState.current
        val captureModel = rememberScreenModel { ResultCaptureScreenModel() }

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
                captureModel.calculating = true
                val result = result.await()
                captureModel.calculating = false
                result
            },
            onFailure = {
                PopAndShowMessageOnFailure(it)
            }
        ) {
            Capturable(captureModel.captureState) {
                resultContent(it)
            }
        }
    }
}