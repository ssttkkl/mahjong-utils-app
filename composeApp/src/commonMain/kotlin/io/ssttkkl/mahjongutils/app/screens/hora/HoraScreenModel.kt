package io.ssttkkl.mahjongutils.app.screens.hora

import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.models.hora.HoraCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import io.ssttkkl.mahjongutils.app.screens.base.FormState


class HoraScreenModel(
    form: HoraFormState? = null
) : FormAndResultScreenModel<HoraArgs, HoraCalcResult>(),
    FormState<HoraArgs> {

    val form = form ?: HoraFormState(screenModelScope)

    override fun fillFormWithArgs(args: HoraArgs, check: Boolean) =
        form.fillFormWithArgs(args, check)

    override fun resetForm() = form.resetForm()
    override fun onCheck(): HoraArgs? = form.onCheck()

    override suspend fun onCalc(args: HoraArgs): HoraCalcResult {
        HoraArgs.history.insert(args)
        return args.calc()
    }

    override val history: HistoryDataStore<HoraArgs>
        get() = HoraArgs.history
}