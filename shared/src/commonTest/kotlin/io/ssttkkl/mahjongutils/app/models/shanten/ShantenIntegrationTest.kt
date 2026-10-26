package io.ssttkkl.mahjongutils.app.models.shanten

import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import mahjongutils.models.Tile
import mahjongutils.shanten.asWithoutGot
import mahjongutils.shanten.asWithGot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ShantenIntegrationTest {
    @Test
    fun exhaustedWinningTileIsNotTenpaiInEitherMode() {
        for (mode in ShantenMode.entries) {
            val result = ShantenArgs(Tile.parseTiles("111z222z333z4444z"), mode).calc()
                .result.shantenInfo.asWithoutGot
            assertEquals(1, result.shantenNum)
            assertEquals(0, result.structuralShantenNum)
            assertTrue(result.advance.isEmpty())
        }
    }

    @Test
    fun discardsDoNotMakeAFifthCopyAvailable() {
        val result = ShantenArgs(Tile.parseTiles("111z222z3333z4444z")).calc()
            .result.shantenInfo.asWithGot
        assertEquals(1, result.shantenNum)
        assertEquals(0, result.structuralShantenNum)
        assertTrue(result.discardToAdvance.values.all { it.shantenNum > 0 })
    }

    @Test
    fun meldSuggestionsAccountForCopiesInTheMeld() {
        val result = FuroChanceShantenArgs(Tile.parseTiles("8999m"), Tile["9m"]).calc()
            .result.shantenInfo
        val exhausted = assertNotNull(result.pon).discardToAdvance.getValue(Tile["8m"])
        assertEquals(1, exhausted.shantenNum)
        assertEquals(0, exhausted.structuralShantenNum)
        assertTrue(exhausted.advance.isEmpty())
        assertEquals(0, assertNotNull(result.minkan).shantenNum)
    }

    @Test
    fun ordinaryWaitRetainsItsAvailableCopies() {
        val result = ShantenArgs(Tile.parseTiles("1112345678999p")).calc()
            .result.shantenInfo.asWithoutGot
        assertEquals(0, result.shantenNum)
        assertEquals(0, result.structuralShantenNum)
        assertEquals(23, result.advanceNum)
    }
}
