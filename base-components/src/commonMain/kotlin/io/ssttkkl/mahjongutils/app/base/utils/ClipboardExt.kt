@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package io.ssttkkl.mahjongutils.app.base.utils

import androidx.compose.foundation.internal.readText
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.setText(text: String?)

suspend fun ClipEntry.getText(): String? {
    return readText()
}