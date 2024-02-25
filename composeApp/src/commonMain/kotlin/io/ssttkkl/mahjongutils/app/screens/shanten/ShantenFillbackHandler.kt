package io.ssttkkl.mahjongutils.app.screens.shanten

import io.ssttkkl.mahjongutils.app.components.resultdisplay.FillbackHandler
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState
import io.ssttkkl.mahjongutils.app.utils.remove

class ShantenFillbackHandler(
    val panelState: EditablePanelState<ShantenFormState, ShantenArgs>,
    val requestFocus: () -> Unit
) : FillbackHandler {
    override fun fillbackShantenAction(action: ShantenAction) {
        val args = panelState.originArgs
        panelState.editing = true
        requestFocus()
        when (action) {
            is ShantenAction.Discard -> {
                val newTiles = args.tiles.remove(action.tile)
                panelState.form.fillFormWithArgs(
                    args.copy(tiles = newTiles)
                )
            }

            is ShantenAction.Ankan -> {
                val newTiles = args.tiles.remove(action.tile, action.tile, action.tile, action.tile)
                panelState.form.fillFormWithArgs(
                    args.copy(tiles = newTiles)
                )
            }

            else -> {}
        }
    }
}