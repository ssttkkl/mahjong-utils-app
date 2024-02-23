package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.AppOptions
import io.ssttkkl.mahjongutils.app.models.base.HistoryDataStore
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.models.hora.HoraCalcResult
import io.ssttkkl.mahjongutils.app.screens.base.FormAndResultScreenModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_agari_not_in_hand
import mahjongutils.composeapp.generated.resources.text_hora_hand_tiles_not_enough
import mahjongutils.composeapp.generated.resources.text_invalid_dora_count
import mahjongutils.composeapp.generated.resources.text_invalid_furo
import mahjongutils.composeapp.generated.resources.text_must_enter_agari
import mahjongutils.composeapp.generated.resources.text_must_enter_tiles
import mahjongutils.hora.HoraOptions
import mahjongutils.models.Furo
import mahjongutils.models.Kan
import mahjongutils.models.Tile
import mahjongutils.models.Wind
import mahjongutils.yaku.Yaku
import mahjongutils.yaku.Yakus
import org.jetbrains.compose.resources.StringResource

class FuroModel {
    var tiles: List<Tile> by mutableStateOf(emptyList())
    var ankan: Boolean by mutableStateOf(false)
    var errMsg by mutableStateOf<StringResource?>(null)

    val isKan: Boolean
        get() = tiles.size == 4 && tiles.all { it == tiles.first() }

    fun toFuro(): Furo {
        return Furo(tiles, ankan)
    }

    companion object {
        fun fromFuro(furo: Furo): FuroModel {
            return FuroModel().apply {
                tiles = furo.tiles
                if (furo is Kan) {
                    ankan = furo.ankan
                }
            }
        }
    }
}

private val yakuConflictingMatrix = mapOf(
    Yakus.Tenhou to setOf(
        Yakus.Chihou,
        Yakus.Richi,
        Yakus.WRichi,
        Yakus.Ippatsu,
        Yakus.Rinshan,
        Yakus.Chankan,
        Yakus.Haitei,
        Yakus.Houtei
    ),
    Yakus.Chihou to setOf(
        Yakus.Tenhou,
        Yakus.Richi,
        Yakus.WRichi,
        Yakus.Ippatsu,
        Yakus.Rinshan,
        Yakus.Chankan,
        Yakus.Haitei,
        Yakus.Houtei
    ),
    Yakus.Richi to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.WRichi
    ),
    Yakus.WRichi to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.Richi
    ),
    Yakus.Ippatsu to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.Rinshan,
        Yakus.Chankan
    ),
    Yakus.Rinshan to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.Ippatsu,
        Yakus.Chankan,
        Yakus.Houtei
    ),
    Yakus.Chankan to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.Ippatsu,
        Yakus.Rinshan,
        Yakus.Haitei
    ),
    Yakus.Haitei to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.Chankan,
        Yakus.Houtei
    ),
    Yakus.Houtei to setOf(
        Yakus.Tenhou,
        Yakus.Chihou,
        Yakus.Rinshan,
        Yakus.Haitei
    )
)

private val combinedYakuConflictingMatrix = mapOf(
    setOf(Yakus.WRichi, Yakus.Ippatsu) to setOf(Yakus.Haitei, Yakus.Houtei)
)

class HoraScreenModel : FormAndResultScreenModel<HoraArgs, HoraCalcResult>() {
    var tiles by mutableStateOf<List<Tile>>(emptyList())
    val furo = mutableStateListOf<FuroModel>()
    var agari by mutableStateOf<Tile?>(null)
    var tsumo by mutableStateOf<Boolean>(true)
    var dora by mutableStateOf<String>("")
    var selfWind by mutableStateOf<Wind?>(null)
    var roundWind by mutableStateOf<Wind?>(null)
    var extraYaku by mutableStateOf<Set<Yaku>>(emptySet())

    val autoDetectedAgari by derivedStateOf {
        if (tiles.size % 3 == 2) {
            tiles.last()
        } else {
            null
        }
    }

    var tilesErrMsg by mutableStateOf<StringResource?>(null)
    var agariErrMsg by mutableStateOf<StringResource?>(null)
    var doraErrMsg by mutableStateOf<StringResource?>(null)

    private var horaOptionsState = mutableStateOf(HoraOptions.Default)
    var horaOptions: HoraOptions
        get() = horaOptionsState.value
        set(value) {
            horaOptionsState.value = value
            screenModelScope.launch {
                AppOptions.datastore.updateData {
                    it.copy(horaOptions = value)
                }
            }
        }

    init {
        screenModelScope.launch {
            AppOptions.datastore.data.collectLatest {
                horaOptionsState.value = it.horaOptions
            }
        }
    }

    override fun resetForm() {
        tiles = emptyList()
        furo.clear()
        agari = null
        tsumo = true
        dora = ""
        selfWind = null
        roundWind = null
        extraYaku = emptySet()

        tilesErrMsg = null
        agariErrMsg = null
        doraErrMsg = null
    }

    private val conflictingYaku by derivedStateOf {
        buildSet {
            extraYaku.forEach {
                yakuConflictingMatrix[it]?.let { addAll(it) }
            }

            combinedYakuConflictingMatrix.forEach {
                if (extraYaku.containsAll(it.key)) {
                    addAll(it.value)
                }
            }
        }
    }

    private fun isYakuAvailable(yaku: Yaku): Boolean {
        if (conflictingYaku.contains(yaku)) {
            return false
        }

        var disabled = false
        when (yaku) {
            Yakus.Tenhou -> {
                disabled = disabled || (selfWind != null && selfWind?.ordinal != 0)
                disabled = disabled || furo.isNotEmpty()
                disabled = disabled || !tsumo
            }

            Yakus.Chihou -> {
                disabled = disabled || (selfWind != null && selfWind?.ordinal == 0)
                disabled = disabled || furo.isNotEmpty()
                disabled = disabled || !tsumo
            }

            Yakus.Richi, Yakus.WRichi -> {
                disabled = disabled || furo.any { !(it.isKan && it.ankan) }
            }

            Yakus.Ippatsu -> {
                disabled = disabled || (Yakus.Richi !in extraYaku && Yakus.WRichi !in extraYaku)
            }

            Yakus.Rinshan -> {
                disabled = disabled || !tsumo || furo.count { it.isKan } == 0
            }

            Yakus.Chankan -> {
                disabled = disabled || tsumo
            }

            Yakus.Haitei -> {
                disabled = disabled || !tsumo
            }

            Yakus.Houtei -> {
                disabled = disabled || tsumo
            }
        }

        return !disabled
    }

    // yaku to enabled
    val allExtraYaku by derivedStateOf {
        listOf(
            Yakus.Tenhou,
            Yakus.Chihou,
            Yakus.WRichi,
            Yakus.Richi,
            Yakus.Ippatsu,
            Yakus.Rinshan,
            Yakus.Chankan,
            Yakus.Haitei,
            Yakus.Houtei
        ).map {
            it to isYakuAvailable(it)
        }
    }

    val unavailableYaku by derivedStateOf {
        allExtraYaku.filter { !it.second }.map { it.first }.toSet()
    }

    override suspend fun onCheck(): Boolean {
        var validTiles = true
        var validAgari = true
        var validDora = true
        var validFuro = true

        val dora = if (dora.isEmpty()) 0 else dora.toIntOrNull()
        if (dora == null) {
            doraErrMsg = Res.string.text_invalid_dora_count
            validDora = false
        }

        if (tiles.isEmpty()) {
            tilesErrMsg = Res.string.text_must_enter_tiles
            validTiles = false
        }

        val curTilesCount = tiles.size + furo.size * 3
        if (curTilesCount != 14 && curTilesCount != 13) {
            tilesErrMsg = Res.string.text_hora_hand_tiles_not_enough
            validTiles = false
        }

        val agari = agari ?: autoDetectedAgari
        if (agari == null) {
            agariErrMsg = Res.string.text_must_enter_agari
            validAgari = false
        } else if (curTilesCount == 14 && agari !in tiles) {
            agariErrMsg = Res.string.text_agari_not_in_hand
            validAgari = false
        }

        furo.forEach {
            try {
                it.toFuro()
                it.errMsg = null
            } catch (e: IllegalArgumentException) {
                it.errMsg = Res.string.text_invalid_furo
            }
        }
        validFuro = furo.all { it.errMsg == null }

        if (validTiles) {
            tilesErrMsg = null
        }
        if (validAgari) {
            agariErrMsg = null
        }
        if (validDora) {
            doraErrMsg = null
        }
        return validTiles && validAgari && validDora && validFuro
    }

    override suspend fun onCalc(): HoraCalcResult {
        val args = HoraArgs(
            tiles,
            furo.map { it.toFuro() },
            (agari ?: autoDetectedAgari)!!,
            tsumo,
            dora.toIntOrNull() ?: 0,
            selfWind,
            roundWind,
            extraYaku,
            horaOptions
        )
        HoraArgs.history.insert(args)
        return args.calc()
    }

    override val history: HistoryDataStore<HoraArgs>
        get() = HoraArgs.history
}