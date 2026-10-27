package io.ssttkkl.mahjongutils.app.components.resultdisplay

import io.ssttkkl.mahjongutils.app.base.utils.percentile
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenMode
import mahjongutils.models.Tile
import mahjongutils.shanten.asWithGot
import mahjongutils.shanten.asWithoutGot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ShantenDisplayTest {
    @Test
    fun fifthCopyWaitOmitsPercentageInEitherMode() {
        for (mode in ShantenMode.entries) {
            val result = ShantenArgs(Tile.parseTiles("1111m123p"), mode).calc()
                .result.shantenInfo.asWithoutGot

            assertEquals(1, result.shantenNum)
            assertEquals(0, result.structuralShantenNum)
            assertEquals(0, result.advanceNum)
            assertEquals(0, result.goodShapeAdvanceNum)
            assertNull(result.goodShapeAdvanceRatio())
        }
    }

    @Test
    fun exhaustedDiscardOmitsPercentageInEitherMode() {
        for (mode in ShantenMode.entries) {
            val result = ShantenArgs(Tile.parseTiles("1111m1234p"), mode).calc()
                .result.shantenInfo.asWithGot
                .discardToAdvance.getValue(Tile["4p"])

            assertEquals(1, result.shantenNum)
            assertEquals(0, result.advanceNum)
            assertEquals(0, result.goodShapeAdvanceNum)
            assertNull(result.goodShapeAdvanceRatio())
        }
    }

    @Test
    fun ordinaryGoodShapePercentageStillFormatsInEitherMode() {
        for (mode in ShantenMode.entries) {
            val result = ShantenArgs(Tile.parseTiles("123m12p45s"), mode).calc()
                .result.shantenInfo.asWithoutGot

            assertEquals(1, result.shantenNum)
            assertEquals(24, result.advanceNum)
            assertEquals(6, result.goodShapeAdvanceNum)
            val ratio = assertNotNull(result.goodShapeAdvanceRatio())
            assertEquals(0.25, ratio)
            assertEquals("25.0", ratio.percentile())
        }
    }
}
