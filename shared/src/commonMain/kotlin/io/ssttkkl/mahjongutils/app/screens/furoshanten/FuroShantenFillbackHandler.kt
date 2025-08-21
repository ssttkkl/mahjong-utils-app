package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.runtime.Stable
import io.ssttkkl.mahjongutils.app.components.resultdisplay.FillbackHandler
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.models.furoshanten.FuroChanceShantenArgs
import io.ssttkkl.mahjongutils.app.screens.common.EditablePanelState
import io.ssttkkl.mahjongutils.app.base.utils.removeLast
import mahjongutils.models.Tile

@Stable
class FuroShantenFillbackHandler(
    val panelState: EditablePanelState<FuroShantenFormState, FuroChanceShantenArgs>,
    val requestFocus: () -> Unit
) : FillbackHandler {
    override fun fillbackAction(action: ShantenAction?, draw: Tile?, discard: Tile?) {
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
                args.tiles.toMutableList()
            }
        }

        draw?.let { newTiles.add(draw) }
        discard?.let { newTiles.removeLast(discard) }
        panelState.form.tiles = newTiles
        panelState.form.chanceTile = null
    }
}