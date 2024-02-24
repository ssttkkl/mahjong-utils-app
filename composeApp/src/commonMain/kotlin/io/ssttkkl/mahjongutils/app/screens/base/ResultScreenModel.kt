package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.Deferred
import org.jetbrains.compose.resources.StringResource

class ResultHolder<RES>(
    var result: Deferred<RES>?,
    val onResultMove: (Deferred<RES>) -> Unit,
) {
    fun moveResult() {
        val resultToMove = result
        result = null
        if (resultToMove != null) {
            onResultMove(resultToMove)
        }
    }
}

class ResultScreenModel : ScreenModel {
    var title by mutableStateOf<StringResource?>(null)
    var resultHolder by mutableStateOf<ResultHolder<*>?>(null)
    var resultContent by mutableStateOf<@Composable (Any) -> Unit>({})

    var calculating by mutableStateOf(true)
}