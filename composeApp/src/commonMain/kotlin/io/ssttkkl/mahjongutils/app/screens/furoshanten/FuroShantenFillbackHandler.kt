package io.ssttkkl.mahjongutils.app.screens.furoshanten

import io.ssttkkl.mahjongutils.app.components.resultdisplay.FillbackHandler
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState
import io.ssttkkl.mahjongutils.app.utils.remove

class FuroShantenFillbackHandler(
    val panelState: EditablePanelState<FuroShantenFormState, FuroChanceShantenArgs>,
    val requestFocus: () -> Unit
) : FillbackHandler {
    override fun fillbackShantenAction(action: ShantenAction) {
        val args = panelState.originArgs
        panelState.editing = true
        panelState.form.fillFormWithArgs(args)
        requestFocus()
        when (action) {
            is ShantenAction.Chi -> {
                val newTiles = args.tiles.remove(
                    action.tatsu.first,
                    action.tatsu.second,
                    action.discard
                )
                panelState.form.tiles = newTiles
                panelState.form.chanceTile = null
            }

            is ShantenAction.Pon -> {
                val newTiles = args.tiles.remove(
                    action.tile,
                    action.tile,
                    action.discard
                )
                panelState.form.tiles = newTiles
                panelState.form.chanceTile = null
            }

            is ShantenAction.Minkan -> {
                val newTiles = args.tiles.remove(
                    action.tile,
                    action.tile,
                    action.tile
                )
                panelState.form.tiles = newTiles
                panelState.form.chanceTile = null
            }

            else -> {}
        }
    }
}