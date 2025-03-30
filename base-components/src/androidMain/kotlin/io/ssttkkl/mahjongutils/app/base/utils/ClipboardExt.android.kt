package io.ssttkkl.mahjongutils.app.base.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.setText(text: String?) {
    val clip = text?.let { ClipEntry(ClipData.newPlainText(text, text)) }
    this.setClipEntry(clip)
}
