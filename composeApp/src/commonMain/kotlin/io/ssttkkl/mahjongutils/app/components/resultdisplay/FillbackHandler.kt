package io.ssttkkl.mahjongutils.app.components.resultdisplay

import mahjongutils.models.Tile

interface FillbackHandler {
    fun fillbackAction(action: ShantenAction) {}
    fun fillbackActionAndDraw(action: ShantenAction, draw: Tile) {}
    fun fillbackActionAndDrawAndDiscard(action: ShantenAction, draw: Tile, discard:Tile) {}
}