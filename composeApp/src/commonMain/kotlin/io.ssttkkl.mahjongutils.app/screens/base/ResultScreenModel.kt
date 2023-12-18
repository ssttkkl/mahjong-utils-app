package io.ssttkkl.mahjongutils.app.screens.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ResultScreenModel<RES> : ScreenModel {
    val result = MutableStateFlow<Deferred<RES>?>(null)

    fun resetResult() {
        result.value = null
    }

    open suspend fun onCheck(appState: AppState): Boolean = true

    abstract suspend fun onCalc(appState: AppState): RES

    suspend fun onSubmit(appState: AppState) {
        if (!onCheck(appState)) {
            return
        }

        result.value = screenModelScope.async(Dispatchers.Default) {
            onCalc(appState)
        }
    }
}