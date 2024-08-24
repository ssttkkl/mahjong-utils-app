package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreenModel
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class FormAndResultScreenModel<ARG, RES> : UrlNavigationScreenModel(), FormState<ARG> {
    var result by mutableStateOf<Deferred<RES>?>(null)

    abstract suspend fun onCalc(args: ARG): RES

    fun onSubmit() {
        val args = onCheck()
        if (args != null) {
            screenModelScope.launch(Dispatchers.Default) {
                result = screenModelScope.async(Dispatchers.Default) {
                    onCalc(args)
                }
            }
        }
    }

    abstract val history: HistoryDataStore<ARG>
}