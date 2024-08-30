package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.rememberCoroutineScope
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowSnackbarOnFailure
import io.ssttkkl.mahjongutils.app.utils.image.ImageUtils
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_share
import mahjongutils.composeapp.generated.resources.text_save_result_success
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
                    ImageUtils.save(img, "result")
                    appState.snackbarHostState.showSnackbar(saveSuccessMessage)
                }
            }
        }) {
            Icon(Icons.Default.Share, stringResource(Res.string.label_share))
        }
    }
}