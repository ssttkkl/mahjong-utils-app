@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.ui.platform.Clipboard
import androidx.compose.foundation.internal.readText

expect suspend fun Clipboard.setText(text: String?)

suspend fun Clipboard.getText(): String? {
    return getClipEntry()?.readText()
}