package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState

expect class SaveResult {
    val isSupportOpen: Boolean
    val isSupportShare: Boolean
    suspend fun open()
    suspend fun share()
}

expect object ImageSaver {
    suspend fun save(appState: AppState, imageBitmap: ImageBitmap, title: String): SaveResult?
}

