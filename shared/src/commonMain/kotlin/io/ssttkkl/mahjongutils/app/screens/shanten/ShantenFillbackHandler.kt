package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.runtime.Stable
import io.ssttkkl.mahjongutils.app.components.resultdisplay.FillbackHandler
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState
import io.ssttkkl.mahjongutils.app.utils.removeLast
import mahjongutils.models.Tile

@Stable
class ShantenFillbackHandler(
    val panelState: EditablePanelState<ShantenFormState, ShantenArgs>,
    val requestFocus: () -> Unit
) : FillbackHandler {
    override fun fillbackAction(action: ShantenAction?, draw: Tile?, discard: Tile?) {
        panelState.editing = true
        requestFocus()
        val args = panelState.originArgs
        val newTiles = when (action) {
            is ShantenAction.Discard -> {
                args.tiles.removeLast(action.tile).toMutableList()
            }
            is ShantenAction.Ankan ->{
                args.tiles.removeLast(action.tile, action.tile, action.tile, action.tile).toMutableList()
            }
            else -> {
                args.tiles.toMutableList()
            }
        }
        draw?.let { newTiles.add(draw) }
        discard?.let { newTiles.remove(discard) }

        panelState.form.fillFormWithArgs(
            args.copy(tiles = newTiles)
        )
    }
}