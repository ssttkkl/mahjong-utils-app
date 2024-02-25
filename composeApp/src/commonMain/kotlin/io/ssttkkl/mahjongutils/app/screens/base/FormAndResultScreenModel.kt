package io.ssttkkl.mahjongutils.app.screens.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class FormAndResultScreenModel<ARG, RES> : ScreenModel, FormState<ARG> {
    val result = MutableStateFlow<Deferred<RES>?>(null)

    fun resetResult() {
        result.value = null
    }

    abstract suspend fun onCalc(args: ARG): RES

    fun onSubmit() {
        val args = onCheck()
        if (args != null) {
            screenModelScope.launch(Dispatchers.Default) {
                result.value = screenModelScope.async(Dispatchers.Default) {
                    onCalc(args)
                }
            }
        }
    }

    abstract val history: HistoryDataStore<ARG>
}