package io.ssttkkl.mahjongutils.app.screens.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class FormAndResultScreenModel<ARG, RES> : ScreenModel {
    val result = MutableStateFlow<Deferred<RES>?>(null)

    open fun resetForm() {

    }

    fun resetResult() {
        result.value = null
    }

    fun postCheck() {
        screenModelScope.launch {
            onCheck()
        }
    }

    open suspend fun onCheck(): Boolean = true

    abstract suspend fun onCalc(appState: AppState): RES

    suspend fun onSubmit(appState: AppState) {
        if (!onCheck()) {
            return
        }

        result.value = screenModelScope.async(Dispatchers.Default) {
            onCalc(appState)
        }
    }

    abstract val history: HistoryDataStore<ARG>

}