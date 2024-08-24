package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.models.AppOptions
import io.ssttkkl.mahjongutils.app.models.hora.HoraArgs
import io.ssttkkl.mahjongutils.app.screens.base.FormState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_agari_not_in_hand
import mahjongutils.composeapp.generated.resources.text_any_tile_must_not_be_more_than_4
import mahjongutils.composeapp.generated.resources.text_hora_hand_tiles_not_enough
import mahjongutils.composeapp.generated.resources.text_invalid_dora_count
import mahjongutils.composeapp.generated.resources.text_invalid_furo
import mahjongutils.composeapp.generated.resources.text_must_enter_agari
import mahjongutils.composeapp.generated.resources.text_must_enter_tiles
import mahjongutils.composeapp.generated.resources.text_too_many_furo
import mahjongutils.hora.HoraArgsErrorInfo
import mahjongutils.hora.HoraOptions
import mahjongutils.hora.validate
import mahjongutils.models.Furo
import mahjongutils.models.Kan
import mahjongutils.models.Tile
import mahjongutils.models.Wind
import mahjongutils.models.toTilesString
import mahjongutils.yaku.Yaku
import mahjongutils.yaku.Yakus
import org.jetbrains.compose.resources.StringResource

@Stable
class FuroModel {
    var tiles: List<Tile> by mutableStateOf(emptyList())
    var ankan: Boolean by mutableStateOf(false)
    var errMsg by mutableStateOf<StringResource?>(null)

    val isKan: Boolean
        get() = tiles.size == 4 && tiles.all { it == tiles.first() }

    fun toFuro(): Furo {
        return Furo(tiles, ankan)
    }

    override fun toString(): String {
        try {
            return toFuro().toString()
        } catch (e: Throwable) {
            return tiles.toTilesString()
        }
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

class HoraFormState(
    val scope: CoroutineScope
) : FormState<HoraArgs> {
    companion object {

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
    }

    var tiles by mutableStateOf<List<Tile>>(emptyList())
    val furo = mutableStateListOf<FuroModel>()
    var agari by mutableStateOf<Tile?>(null)
    var tsumo by mutableStateOf<Boolean>(true)
    var dora by mutableStateOf<String>("")
    var selfWind by mutableStateOf<Wind?>(null)
    var roundWind by mutableStateOf<Wind?>(null)
    var extraYaku by mutableStateOf<Set<Yaku>>(emptySet())

    override fun applyFromMap(map: Map<String, String>) {
        map["tiles"]?.let {
            runCatching {
                tiles = Tile.parseTiles(it)
            }
        }
        map["furo"]?.let {
            runCatching {
                furo.clear()
                furo.addAll(it.split(",").filter { it.isNotEmpty() }
                    .map { FuroModel.fromFuro(Furo.parse(it)) })
            }
        }
        map["agari"]?.let {
            runCatching {
                agari = Tile.get(it)
            }
        }
        map["tsumo"]?.let {
            runCatching {
                tsumo = it.toBoolean()
            }
        }
        map["dora"]?.let {
            runCatching {
                dora = it
            }
        }
        map["selfWind"]?.let {
            runCatching {
                selfWind = Wind.entries.first { e -> e.name == it }
            }
        }
        map["roundWind"]?.let {
            runCatching {
                roundWind = Wind.entries.first { e -> e.name == it }
            }
        }
        map["extraYaku"]?.let {
            runCatching {
                extraYaku = it.split(",").filter { it.isNotEmpty() }
                    .map {
                        Yakus.allExtraYaku.first { e -> e.name == it }
                    }.toSet()
            }
        }
    }

    override fun extractToMap(): Map<String, String> {
        return buildMap {
            put("tiles", tiles.toTilesString())
            put("furo", furo.joinToString(",") { it.toString() })
            agari?.let { agari ->
                put("agari", agari.toString())
            }
            put("tsumo", tsumo.toString())
            put("dora", dora)
            selfWind?.let { selfWind ->
                put("selfWind", selfWind.toString())
            }
            roundWind?.let { roundWind ->
                put("roundWind", roundWind.toString())
            }
            put("extraYaku", extraYaku.joinToString(",") { it.name })
        }
    }

    val autoDetectedAgari by derivedStateOf {
        // 当输入k*3+2张牌时，自动将最后一张作为默认的所和的牌
        if (tiles.size % 3 == 2) {
            tiles.last()
        } else {
            null
        }
    }

    val tilesErrMsg = mutableStateListOf<StringResource>()
    val furoErrMsg = mutableStateListOf<StringResource>()
    val agariErrMsg = mutableStateListOf<StringResource>()
    val doraErrMsg = mutableStateListOf<StringResource>()

    private var horaOptionsState = mutableStateOf(HoraOptions.Default)
    var horaOptions: HoraOptions
        get() = horaOptionsState.value
        set(value) {
            horaOptionsState.value = value
            scope.launch {
                AppOptions.datastore.updateData {
                    it.copy(horaOptions = value)
                }
            }
        }

    init {
        scope.launch {
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

        tilesErrMsg.clear()
        furoErrMsg.clear()
        agariErrMsg.clear()
        doraErrMsg.clear()
    }

    override fun fillFormWithArgs(args: HoraArgs, check: Boolean) {
        tiles = args.tiles
        furo.clear()
        furo.addAll(args.furo.map(FuroModel::fromFuro))
        agari = args.agari
        tsumo = args.tsumo
        dora = args.dora.toString()
        selfWind = args.selfWind
        roundWind = args.roundWind
        extraYaku = args.extraYaku
        horaOptions = args.options

        if (check) {
            onCheck()
        }
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

    @Suppress("UNCHECKED_CAST")
    override fun onCheck(): HoraArgs? {
        tilesErrMsg.clear()
        furoErrMsg.clear()
        agariErrMsg.clear()
        doraErrMsg.clear()

        // 事前校验
        val tiles = if (tiles.size % 3 == 1 && agari != null) {
            // 如果输入是k*3+1张牌，则自动将所和的牌纳入手牌
            tiles + agari!!
        } else {
            tiles
        }

        val agari = agari ?: autoDetectedAgari
        if (agari == null) {
            agariErrMsg.add(Res.string.text_must_enter_agari)
        }

        val dora = if (dora.isEmpty()) 0 else dora.toIntOrNull()
        if (dora == null) {
            doraErrMsg.add(Res.string.text_invalid_dora_count)
        }

        var validFuro = true
        val parsedFuro = furo.map {
            try {
                val fr = it.toFuro()
                it.errMsg = null
                fr
            } catch (e: IllegalArgumentException) {
                it.errMsg = Res.string.text_invalid_furo
                validFuro = false
                null
            }
        }

        // 调库校验
        if (tilesErrMsg.isEmpty() && furoErrMsg.isEmpty() && agariErrMsg.isEmpty() && validFuro) {
            val muArgs = mahjongutils.hora.HoraArgs(
                tiles,
                parsedFuro as List<Furo>,
                agari!!,
                tsumo,
                dora ?: 0,
                selfWind,
                roundWind,
                extraYaku
            )
            val errors = muArgs.validate()
            for (it in errors) {
                when (it) {
                    HoraArgsErrorInfo.tilesIsEmpty -> {
                        tilesErrMsg.add(Res.string.text_must_enter_tiles)
                    }

                    HoraArgsErrorInfo.tooManyFuro -> {
                        furoErrMsg.add(Res.string.text_too_many_furo)
                    }

                    HoraArgsErrorInfo.anyTileMoreThan4 -> {
                        tilesErrMsg.add(Res.string.text_any_tile_must_not_be_more_than_4)
                    }

                    HoraArgsErrorInfo.tilesNumIllegal -> {
                        tilesErrMsg.add(Res.string.text_hora_hand_tiles_not_enough)
                    }

                    HoraArgsErrorInfo.agariNotInTiles -> {
                        agariErrMsg.add(Res.string.text_agari_not_in_hand)
                    }

                    else -> {}
                }
            }
        }
        if (tilesErrMsg.isEmpty() && furoErrMsg.isEmpty() && agariErrMsg.isEmpty() && doraErrMsg.isEmpty() && validFuro) {
            return HoraArgs(
                tiles,
                furo.map { it.toFuro() },
                (agari ?: autoDetectedAgari)!!,
                tsumo,
                dora ?: 0,
                selfWind,
                roundWind,
                extraYaku,
                horaOptions
            )
        } else {
            return null
        }
    }
}