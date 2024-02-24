package io.ssttkkl.mahjongutils.app.screens.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
        screenModelScope.launch(Dispatchers.Default) {
            onCheck()
        }
    }

    open fun onCheck(): Boolean = true

    abstract suspend fun onCalc(): RES

    fun onSubmit() {
        if (onCheck()) {
            screenModelScope.launch(Dispatchers.Default) {
                result.value = screenModelScope.async(Dispatchers.Default) {
                    onCalc()
                }
            }
        }
    }

    abstract val history: HistoryDataStore<ARG>

}