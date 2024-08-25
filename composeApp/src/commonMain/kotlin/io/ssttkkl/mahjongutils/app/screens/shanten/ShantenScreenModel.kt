package io.ssttkkl.mahjongutils.app.screens.shanten

import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import io.ssttkkl.mahjongutils.app.screens.base.FormState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import mahjongutils.models.toTilesString

class ShantenScreenModel(
    val form: ShantenFormState = ShantenFormState()
) : FormAndResultScreenModel<ShantenArgs, ShantenCalcResult>(),
    FormState<ShantenArgs> by form {
    override suspend fun onCalc(args: ShantenArgs): ShantenCalcResult {
        screenModelScope.launch(Dispatchers.Default + NonCancellable) {
            ShantenArgs.history.insert(args)
        }
        return args.calc()
    }

    override val history: HistoryDataStore<ShantenArgs>
        get() = ShantenArgs.history

    override fun extractToMap(): Map<String, String> {
        return lastArg?.let {
            mapOf(
                "tiles" to it.tiles.toTilesString(),
                "shantenMode" to it.mode.name
            )
        } ?: emptyMap()
    }
}