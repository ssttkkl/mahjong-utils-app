package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreenModel
import io.ssttkkl.mahjongutils.app.models.hanhu.HanHuArgs
import io.ssttkkl.mahjongutils.app.models.hanhu.HanHuResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class HanhuScreenModel(
    form: HanhuFormState? = null
) : UrlNavigationScreenModel() {
    val form: HanhuFormState = form ?: HanhuFormState(screenModelScope)

    var lastArgs: HanHuArgs? by mutableStateOf(null)
    var result: Deferred<HanHuResult>? by mutableStateOf(null)

    fun onSubmit() {
        val args = form.onCheck()
        if (args == null) {
            result = null
        } else if (args != lastArgs) {
            lastArgs = args
            result = screenModelScope.async(Dispatchers.Default) {
                args.calc()
            }
        }
    }
}