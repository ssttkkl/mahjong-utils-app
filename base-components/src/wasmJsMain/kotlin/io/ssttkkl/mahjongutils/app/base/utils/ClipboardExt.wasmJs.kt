package io.ssttkkl.mahjongutils.app.base.utils

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.setText(text: String?) {
    val entry = text?.let { ClipEntry.withPlainText(it) }
    this.setClipEntry(entry)
}
