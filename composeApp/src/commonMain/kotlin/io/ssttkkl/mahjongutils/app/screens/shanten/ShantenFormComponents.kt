package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.basic.RatioGroups
import io.ssttkkl.mahjongutils.app.components.basic.RatioOption
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenMode
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_regular_shanten
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
import mahjongutils.composeapp.generated.resources.label_union_shanten
import mahjongutils.composeapp.generated.resources.text_regular_shanten_desc
import mahjongutils.composeapp.generated.resources.text_union_shanten_desc
import org.jetbrains.compose.resources.stringResource

class ShantenFormComponents(val form: ShantenFormState) {
    @Composable
    fun Tiles(modifier: Modifier = Modifier) {
        ValidationField(form.tilesErrMsg, modifier) { isError ->
            TileField(
                value = form.tiles,
                onValueChange = { form.tiles = it },
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                label = stringResource(Res.string.label_tiles_in_hand)
            )
        }
    }


    @Composable
    private fun ShantenModeRatioGroups(
        value: ShantenMode,
        onValueChanged: (ShantenMode) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val radioOptions = listOf(
            RatioOption(
                ShantenMode.Union,
                stringResource(Res.string.label_union_shanten),
                stringResource(Res.string.text_union_shanten_desc)
            ),
            RatioOption(
                ShantenMode.Regular,
                stringResource(Res.string.label_regular_shanten),
                stringResource(Res.string.text_regular_shanten_desc)
            ),
        )

        RatioGroups(radioOptions, value, onValueChanged, modifier)
    }

    @Composable
    fun ShantenMode() {
        ShantenModeRatioGroups(
            form.shantenMode,
            { form.shantenMode = it }
        )
    }
}