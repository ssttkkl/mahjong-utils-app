package io.ssttkkl.mahjongutils.app.screens.hora

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import mahjongutils.models.Tile
import mahjongutils.yaku.Yakus
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HoraFormStateTest {
    // No datastore collector or writes are needed for form validation.
    private fun form() = HoraFormState(CoroutineScope(Job().apply { cancel() })).apply {
        tsumo = false
        tiles = Tile.parseTiles("234m456p789s11z23m")
        agari = Tile["1m"]
    }

    @Test
    fun riichiIppatsuChankanWorksInBothSelectionOrdersAndScores() {
        val form = form()
        form.extraYaku = setOf(Yakus.Richi, Yakus.Ippatsu)
        assertFalse(Yakus.Chankan in form.unavailableYaku)
        form.extraYaku = setOf(Yakus.Richi, Yakus.Chankan)
        assertFalse(Yakus.Ippatsu in form.unavailableYaku)
        form.extraYaku += Yakus.Ippatsu
        val args = assertNotNull(form.onCheck())
        // Exercise the same published-core entry point as HoraArgs.calc().
        val result = mahjongutils.hora.hora(args.tiles, args.furo, args.agari,
            args.tsumo, args.dora, args.selfWind, args.roundWind, args.extraYaku, args.options)
        assertTrue(result.yaku.containsAll(setOf(Yakus.Richi, Yakus.Ippatsu, Yakus.Chankan)))
    }

    @Test
    fun chankanRejectsAnotherConcealedCopyForBothInputLengths() {
        val form = form()
        form.tiles = Tile.parseTiles("123m456p789s11z23m")
        form.extraYaku = setOf(Yakus.Chankan)
        assertTrue(Yakus.Chankan in form.unavailableYaku) // 13 tiles, contains 1m
        assertNull(form.onCheck())
        form.tiles += Tile["1m"]
        assertTrue(Yakus.Chankan in form.unavailableYaku) // 14 tiles, two 1m
        assertNull(form.onCheck())
        assertTrue(form.agariErrMsg.isNotEmpty())
    }

    @Test
    fun chankanAcceptsOnlyWinningCopyAndAutoDetectedAgari() {
        val form = form()
        form.tiles = Tile.parseTiles("234m456p789s11z23m")
        form.extraYaku = setOf(Yakus.Chankan)
        assertFalse(Yakus.Chankan in form.unavailableYaku)
        assertNotNull(form.onCheck())
        form.tiles += Tile["1m"]
        form.agari = null
        assertFalse(Yakus.Chankan in form.unavailableYaku)
        assertNotNull(form.onCheck())
    }

    @Test
    fun chankanRejectsCopiesInOpenAndConcealedMelds() {
        for (ankan in listOf(false, true)) {
            val form = form()
            form.tiles = Tile.parseTiles("234m789s11z23m")
            form.furo += FuroModel().apply {
                tiles = Tile.parseTiles(if (ankan) "1111m" else "123m")
                this.ankan = ankan
            }
            form.extraYaku = setOf(Yakus.Chankan)
            assertTrue(Yakus.Chankan in form.unavailableYaku)
            assertNull(form.onCheck())
        }
    }

    @Test
    fun restoredChankanCannotBypassSubmissionValidation() {
        val form = form()
        form.applyFromMap(mapOf(
            "tiles" to "123m456p789s11z23m",
            "agari" to "1m",
            "tsumo" to "false",
            "extraYaku" to Yakus.Chankan.name
        ))
        assertNull(form.onCheck())
        assertTrue(form.agariErrMsg.isNotEmpty())
    }

    @Test
    fun redAndOrdinaryFivesCountAsTheSameTile() {
        for ((held, winning) in listOf("0m" to "5m", "5m" to "0m")) {
            val form = form()
            form.tiles = Tile.parseTiles("234m456p789s11z3m") + Tile[held]
            form.agari = Tile[winning]
            form.extraYaku = setOf(Yakus.Chankan)
            assertTrue(Yakus.Chankan in form.unavailableYaku)
            assertNull(form.onCheck())
            form.tiles += Tile[winning]
            assertTrue(Yakus.Chankan in form.unavailableYaku)
            assertNull(form.onCheck())
        }
    }

    @Test
    fun kokushiMissingTileCanBeRobbedButThirteenSidedWaitCannot() {
        val form = form()
        form.tiles = Tile.parseTiles("19m19p19s1234566z")
        form.agari = Tile["7z"]
        form.extraYaku = setOf(Yakus.Chankan)
        assertFalse(Yakus.Chankan in form.unavailableYaku)
        assertNotNull(form.onCheck())
        form.tiles = Tile.parseTiles("19m19p19s1234567z")
        assertTrue(Yakus.Chankan in form.unavailableYaku)
        assertNull(form.onCheck())
    }

    @Test
    fun ordinaryRonAndExistingTsumoRestrictionsArePreserved() {
        val form = form()
        form.tiles = Tile.parseTiles("123m456p789s11z23m")
        assertNotNull(form.onCheck()) // duplicate 1m is legal without chankan
        form.tsumo = true
        assertTrue(Yakus.Chankan in form.unavailableYaku)
        form.extraYaku = setOf(Yakus.Richi, Yakus.Ippatsu)
        assertTrue(Yakus.Rinshan in form.unavailableYaku)
    }
}
