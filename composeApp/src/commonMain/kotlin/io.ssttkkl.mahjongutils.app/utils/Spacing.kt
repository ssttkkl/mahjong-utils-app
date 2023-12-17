package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val windowHorizontalMargin: Dp,
    val panesHorizontalSpacing: Dp,
    val panelsVerticalSpacing: Dp,
    val cardInnerPadding: PaddingValues,
) {
    fun Modifier.windowHorizontalMargin() = padding(horizontal = windowHorizontalMargin)

    @Composable
    fun VerticalSpacerBetweenPanels() {
        Spacer(Modifier.height(panelsVerticalSpacing))
    }

    companion object {
        val current
            @Composable
            get() = LocalSpacing.current

        val compactSpacing = Spacing(
            windowHorizontalMargin = 16.dp,
            panesHorizontalSpacing = 16.dp,
            panelsVerticalSpacing = 24.dp,
            cardInnerPadding = PaddingValues(8.dp, 16.dp)
        )

        val mediumSpacing = Spacing(
            windowHorizontalMargin = 24.dp,
            panesHorizontalSpacing = 24.dp,
            panelsVerticalSpacing = 32.dp,
            cardInnerPadding = PaddingValues(12.dp, 24.dp)
        )

        val expandedSpacing = Spacing(
            windowHorizontalMargin = 24.dp,
            panesHorizontalSpacing = 24.dp,
            panelsVerticalSpacing = 32.dp,
            cardInnerPadding = PaddingValues(12.dp, 24.dp)
        )
    }
}

val LocalSpacing = compositionLocalOf { Spacing.compactSpacing }
