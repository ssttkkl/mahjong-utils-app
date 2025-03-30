package io.ssttkkl.mahjongutils.app.base.utils

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import java.awt.datatransfer.StringSelection

actual suspend fun Clipboard.setText(text: String?) {
    val entry = text?.let { ClipEntry(StringSelection(it)) }
    this.setClipEntry(entry)
}
