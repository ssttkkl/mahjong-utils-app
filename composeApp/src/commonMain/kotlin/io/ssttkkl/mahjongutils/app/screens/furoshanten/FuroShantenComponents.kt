package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.base.components.SwitchItem
import io.ssttkkl.mahjongutils.app.components.tile.OutlinedTileField
import io.ssttkkl.mahjongutils.app.components.tile.TileField
import io.ssttkkl.mahjongutils.app.base.components.ValidationField
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_allow_chi
import mahjongutils.composeapp.generated.resources.label_tile_discarded_by_other
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
import org.jetbrains.compose.resources.stringResource

@Immutable
class FuroShantenComponents(
    val form: FuroShantenFormState
) {
    @Composable
    fun Tiles(modifier: Modifier = Modifier, forResultContent: Boolean = false) {
        ValidationField(form.tilesErrMsg, modifier) { isError ->
            if (forResultContent) {
                TileField(
                    value = form.tiles,
                    onValueChange = { form.tiles = it },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    label = stringResource(Res.string.label_tiles_in_hand)
                )
            } else {
                OutlinedTileField(
                    value = form.tiles,
                    onValueChange = { form.tiles = it },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    label = stringResource(Res.string.label_tiles_in_hand)
                )
            }
        }
    }

    @Composable
    fun ChanceTile(modifier: Modifier = Modifier, forResultContent: Boolean = false) {
        ValidationField(form.chanceTileErrMsg, modifier) { isError ->
            if (forResultContent) {
                TileField(
                    value = form.chanceTile?.let { listOf(it) } ?: emptyList(),
                    onValueChange = { form.chanceTile = it.firstOrNull() },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    label = stringResource(Res.string.label_tile_discarded_by_other)
                )
            } else {
                OutlinedTileField(
                    value = form.chanceTile?.let { listOf(it) } ?: emptyList(),
                    onValueChange = { form.chanceTile = it.firstOrNull() },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    label = stringResource(Res.string.label_tile_discarded_by_other)
                )
            }
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