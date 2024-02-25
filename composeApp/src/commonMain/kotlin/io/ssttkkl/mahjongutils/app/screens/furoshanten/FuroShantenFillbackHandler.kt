package io.ssttkkl.mahjongutils.app.screens.furoshanten

import io.ssttkkl.mahjongutils.app.components.resultdisplay.FillbackHandler
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState
import io.ssttkkl.mahjongutils.app.utils.removeLast
import mahjongutils.models.Tile

class FuroShantenFillbackHandler(
    val panelState: EditablePanelState<FuroShantenFormState, FuroChanceShantenArgs>,
    val requestFocus: () -> Unit
) : FillbackHandler {
    private fun fillbackAction(action: ShantenAction, draw: Tile?, discard: Tile?) {
        val args = panelState.originArgs
        panelState.editing = true
        panelState.form.fillFormWithArgs(args)
        requestFocus()
        val newTiles = when (action) {
            is ShantenAction.Chi -> {
                args.tiles.removeLast(
                    action.tatsu.first,
                    action.tatsu.second,
                    action.discard
                ).toMutableList()
            }

            is ShantenAction.Pon -> {
                args.tiles.removeLast(
                    action.tile,
                    action.tile,
                    action.discard
                ).toMutableList()
            }

            is ShantenAction.Minkan -> {
                args.tiles.removeLast(
                    action.tile,
                    action.tile,
                    action.tile
                ).toMutableList()
            }

            else -> {
                arrayListOf()
            }
        }

        draw?.let { newTiles.add(draw) }
        discard?.let { newTiles.removeLast(discard) }
        panelState.form.tiles = newTiles
        panelState.form.chanceTile = null
    }

    override fun fillbackAction(action: ShantenAction) {
        fillbackAction(action, null, null)
    }

    override fun fillbackActionAndDraw(action: ShantenAction, draw: Tile) {
        fillbackAction(action, draw, null)
    }

    override fun fillbackActionAndDrawAndDiscard(action: ShantenAction, draw: Tile, discard: Tile) {
        fillbackAction(action, draw, discard)
    }
}