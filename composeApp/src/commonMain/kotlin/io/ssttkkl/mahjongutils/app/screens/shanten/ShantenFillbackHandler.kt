package io.ssttkkl.mahjongutils.app.screens.shanten

import io.ssttkkl.mahjongutils.app.components.resultdisplay.FillbackHandler
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.models.shanten.ShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState

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
                val lastIndexOfTile = args.tiles.lastIndexOf(action.tile)
                val newTiles = args.tiles.toMutableList().apply {
                    removeAt(lastIndexOfTile)
                }
                panelState.form.fillFormWithArgs(
                    args.copy(tiles = newTiles)
                )
            }

            is ShantenAction.Ankan -> {
                panelState.form.fillFormWithArgs(
                    args.copy(tiles = args.tiles.filter { it != action.tile })
                )
            }

            else -> {}
        }
    }
}