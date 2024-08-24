package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowSnackbarOnFailure

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