package io.ssttkkl.mahjongutils.app.screens.hora

import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.models.hora.HoraCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import io.ssttkkl.mahjongutils.app.screens.base.FormState
import mahjongutils.models.toTilesString


class HoraScreenModel(
    form: HoraFormState? = null
) : FormAndResultScreenModel<HoraArgs, HoraCalcResult>(),
    FormState<HoraArgs> {

    val form = form ?: HoraFormState(screenModelScope)

    override fun fillFormWithArgs(args: HoraArgs, check: Boolean) =
        form.fillFormWithArgs(args, check)

    override fun resetForm() = form.resetForm()
    override fun onCheck(): HoraArgs? = form.onCheck()

    override fun applyFromMap(map: Map<String, String>) = form.applyFromMap(map)

    override fun extractToMap(): Map<String, String> {
        return lastArg?.let {
            buildMap {
                put("tiles", it.tiles.toTilesString())
                put("furo", it.furo.joinToString(",") { it.toString() })
                put("agari", it.agari.toString())
                put("tsumo", it.tsumo.toString())
                put("dora", it.dora.toString())
                it.selfWind?.let { selfWind ->
                    put("selfWind", selfWind.toString())
                }
                it.roundWind?.let { roundWind ->
                    put("roundWind", roundWind.toString())
                }
                put("extraYaku", it.extraYaku.joinToString(",") { it.name })
            }
        } ?: emptyMap()
    }

    override suspend fun onCalc(args: HoraArgs): HoraCalcResult {
        HoraArgs.history.insert(args)
        return args.calc()
    }

    override val history: HistoryDataStore<HoraArgs>
        get() = HoraArgs.history
}