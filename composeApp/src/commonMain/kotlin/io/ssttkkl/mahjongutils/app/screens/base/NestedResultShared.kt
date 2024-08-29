package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import dev.shreyaspatil.capturable.capturable
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppBottomSheetState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowSnackbarOnFailure
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.image.SavePhotoUtils
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_history_outlined
import mahjongutils.composeapp.generated.resources.label_clear
import mahjongutils.composeapp.generated.resources.label_history
import mahjongutils.composeapp.generated.resources.label_share
import mahjongutils.composeapp.generated.resources.text_save_result_success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun <ARG, RES> NestedResultCalculation(model: NestedResultScreenModel<ARG, RES>) {
    model.result?.let { resultDeferred ->
        Calculation(
            resultDeferred,
            {
                resultDeferred.await()
            },
            onFailure = {
                PopAndShowSnackbarOnFailure(it)
            }
        ) { result ->
            result?.let { model.resultContent(result) }
        }
    }
}

@OptIn(ExperimentalComposeApi::class)
@Composable
fun <ARG, RES> NestedResultTopBarActions(model: NestedResultScreenModel<ARG, RES>) {
    val coroutineScope = rememberCoroutineScope()
    val appState = LocalAppState.current

    val saveSuccessMessage = stringResource(Res.string.text_save_result_success)
    if (model.result != null) {
        IconButton(onClick = {
            model.parentScreenModel?.resultCaptureController?.let { controller ->
                coroutineScope.launch {
                    val img = controller.captureAsync().await()
                    SavePhotoUtils.save(img, "result")
                    appState.snackbarHostState.showSnackbar(saveSuccessMessage)
                }
            }
        }) {
            Icon(Icons.Default.Share, stringResource(Res.string.label_share))
        }
    }
}