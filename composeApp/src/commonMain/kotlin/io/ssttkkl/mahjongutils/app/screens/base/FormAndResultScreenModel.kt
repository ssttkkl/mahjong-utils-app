package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ssttkkl.mahjongutils.app.base.utils.logger
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreenModel
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@Stable
abstract class FormAndResultScreenModel<ARG, RES> : UrlNavigationScreenModel(), FormState<ARG> {
    var onResult by mutableStateOf<(Deferred<RES>) -> Unit>({
        KotlinLogging.logger(this::class).debug("onResult not set")
    })

    abstract suspend fun onCalc(args: ARG): RES

    fun onSubmit() {
        val args = onCheck()
        if (args != null) {
            lastArg = args
            onResult(screenModelScope.async(Dispatchers.Default) {
                onCalc(args)
            })
        }
    }

    abstract val history: HistoryDataStore<ARG>?

    var lastArg: ARG? by mutableStateOf(null)
        private set

    abstract fun extractToMap(): Map<String, String>
}