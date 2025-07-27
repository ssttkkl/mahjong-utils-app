package io.ssttkkl.mahjongutils.app.screens.furoshanten

import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import io.ssttkkl.mahjongutils.app.screens.base.FormState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import mahjongutils.models.toTilesString

class FuroShantenScreenModel(
    val form: FuroShantenFormState = FuroShantenFormState()
) : FormAndResultScreenModel<FuroChanceShantenArgs, FuroChanceShantenCalcResult>(),
    FormState<FuroChanceShantenArgs> by form {

    override suspend fun onCalc(args: FuroChanceShantenArgs): FuroChanceShantenCalcResult {
        screenModelScope.launch(Dispatchers.Default + NonCancellable) {
            FuroChanceShantenArgs.history.insert(args)
        }
        return args.calc()
    }

    override val history: HistoryDataStore<FuroChanceShantenArgs>
        get() = FuroChanceShantenArgs.history

    override fun extractToMap(): Map<String, String> {
        return lastArg?.let {
            buildMap {
                put("tiles", it.tiles.toTilesString())
                put("chanceTile", it.chanceTile.toString())
                put("allowChi", it.allowChi.toString())
            }
        } ?: emptyMap()
    }
}