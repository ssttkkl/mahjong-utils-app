package io.ssttkkl.mahjongutils.app.init

data class InitModule(
    val priority: Int,
    val onInit: () -> Unit
)

object AppInit {
    var commonModules = listOf(
        LogInit
    )

    fun doInit() {
        (commonModules + platformModules).sortedBy { it.priority }
            .forEach {
                it.onInit()
            }
    }
}

internal expect val AppInit.platformModules: List<InitModule>

internal expect val LogInit: InitModule