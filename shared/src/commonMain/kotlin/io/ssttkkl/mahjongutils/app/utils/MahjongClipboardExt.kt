package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import io.ssttkkl.mahjongutils.app.base.utils.getText
import io.ssttkkl.mahjongutils.app.base.utils.setText
import mahjongutils.models.Tile
import mahjongutils.models.toTilesString

suspend fun Clipboard.readTiles(): List<Tile>? {
    return getClipEntry()?.parseTiles()
}

suspend fun ClipEntry.parseTiles(): List<Tile>? {
    val text = getText() ?: return emptyList()
    return runCatching { Tile.parseTiles(text) }.getOrNull()
}

suspend fun Clipboard.writeTiles(data: List<Tile>?) {
    data?.let {
        setText(it.toTilesString())
    }
}