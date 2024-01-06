package io.ssttkkl.mahjongutils.app.models.hora

import androidx.datastore.core.DataStore
import io.ssttkkl.mahjongutils.app.utils.FileUtils
import io.ssttkkl.mahjongutils.app.utils.createDatastore
import mahjongutils.hora.HoraOptions


object HoraOptionsStore : DataStore<HoraOptions> by createDatastore(
    HoraOptions.Default,
    producePath = { FileUtils.sandboxPath / "prefer_hora_options.json" }
)
