package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.basic.SwitchItem
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.components.validation.ValidationField
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_allow_chi
import mahjongutils.composeapp.generated.resources.label_tile_discarded_by_other
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
import org.jetbrains.compose.resources.stringResource

class FuroShantenComponents(
    val form: FuroShantenFormState
) {
    @Composable
    fun Tiles() {
        ValidationField(form.tilesErrMsg) { isError ->
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
    fun ChanceTile() {
        ValidationField(form.chanceTileErrMsg) { isError ->
            TileField(
                value = form.chanceTile?.let { listOf(it) } ?: emptyList(),
                onValueChange = { form.chanceTile = it.firstOrNull() },
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                label = stringResource(Res.string.label_tile_discarded_by_other)
            )
        }
    }

    @Composable
    fun AllowChi() {
        SwitchItem(
            form.allowChi,
            { form.allowChi = it },
            stringResource(Res.string.label_allow_chi)
        )
    }
}