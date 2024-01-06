package io.ssttkkl.mahjongutils.app.models

import io.ssttkkl.mahjongutils.app.utils.FileUtils
import io.ssttkkl.mahjongutils.app.utils.createDatastore
import kotlinx.serialization.Serializable
import mahjongutils.hanhu.HanHuOptions
import mahjongutils.hora.HoraOptions

@Serializable
data class AppOptions(
    val horaOptions: HoraOptions = HoraOptions.Default,
    val hanHuOptions: HanHuOptions = HanHuOptions.Default
) {
    companion object {
        val datastore = createDatastore(
            AppOptions(),
            producePath = { FileUtils.sandboxPath / "app_options.json" }
        )
    }
}