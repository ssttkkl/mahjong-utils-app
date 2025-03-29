package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

@OptIn(ExperimentalComposeUiApi::class)
actual suspend fun Clipboard.setText(text: String?) {
    val entry = text?.let { ClipEntry.withPlainText(it) }
    this.setClipEntry(entry)
}
