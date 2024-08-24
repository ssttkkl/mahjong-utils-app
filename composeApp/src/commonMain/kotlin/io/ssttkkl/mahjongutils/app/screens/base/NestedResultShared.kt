package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowSnackbarOnFailure

class NestedResultShared<ARG, RES>(val model: NestedResultScreenModel<ARG, RES>) {

    @Composable
    fun ResultCalculation() {
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
}