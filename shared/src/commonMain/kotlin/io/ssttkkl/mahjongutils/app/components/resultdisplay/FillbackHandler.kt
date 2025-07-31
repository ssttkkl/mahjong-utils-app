package io.ssttkkl.mahjongutils.app.components.resultdisplay

import mahjongutils.models.Tile

interface FillbackHandler {
    fun fillbackAction(action: ShantenAction?, draw: Tile?, discard: Tile?)

    fun fillbackDraw(draw: Tile) {
        fillbackAction(null, draw, null)
    }

    fun fillbackAction(action: ShantenAction) {
        fillbackAction(action, null, null)
    }

    fun fillbackActionAndDraw(action: ShantenAction, draw: Tile) {
        fillbackAction(action, draw, null)
    }

    fun fillbackActionAndDrawAndDiscard(action: ShantenAction, draw: Tile, discard: Tile) {
        fillbackAction(action, draw, discard)
    }
}