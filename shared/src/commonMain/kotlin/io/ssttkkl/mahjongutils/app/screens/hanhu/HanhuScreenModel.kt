package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.hanhu.HanHuArgs
import io.ssttkkl.mahjongutils.app.models.hanhu.HanHuResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import kotlinx.coroutines.Deferred


class HanhuScreenModel(
    form: HanhuFormState? = null
) : FormAndResultScreenModel<HanHuArgs, HanHuResult>() {
    val form: HanhuFormState = form ?: HanhuFormState(screenModelScope)

    var result: Deferred<HanHuResult>? by mutableStateOf(null)

    init {
        onResult = {
            result = it
        }
    }

    override fun onCheck(): HanHuArgs? = form.onCheck()

    override fun resetForm() = form.resetForm()

    override fun fillFormWithArgs(args: HanHuArgs, check: Boolean) =
        form.fillFormWithArgs(args, check)

    override suspend fun onCalc(args: HanHuArgs): HanHuResult {
        return args.calc()
    }

    override val history: HistoryDataStore<HanHuArgs>?
        get() = null

    override fun applyFromMap(map: Map<String, String>) = form.applyFromMap(map)

    override fun extractToMap(): Map<String, String> {
        return lastArg?.let {
            mapOf(
                "han" to it.han.toString(),
                "hu" to it.hu.toString()
            )
        } ?: emptyMap()
    }
}