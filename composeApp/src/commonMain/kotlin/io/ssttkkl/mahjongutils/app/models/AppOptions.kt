package io.ssttkkl.mahjongutils.app.models

import io.ssttkkl.mahjongutils.app.base.utils.createDataStore
import kotlinx.serialization.Serializable
import mahjongutils.hanhu.HanHuOptions
import mahjongutils.hora.HoraOptions

@Serializable
data class AppOptions(
    val horaOptions: HoraOptions = HoraOptions.Default,
    val hanHuOptions: HanHuOptions = HanHuOptions.Default
) {
    companion object {
        val datastore = createDataStore(
            "app_options",
            null,
            AppOptions(),
        )
    }
}