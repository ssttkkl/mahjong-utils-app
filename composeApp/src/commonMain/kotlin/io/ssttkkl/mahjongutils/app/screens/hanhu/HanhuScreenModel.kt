package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.MR
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import mahjongutils.hanhu.ChildPoint
import mahjongutils.hanhu.ParentPoint
import mahjongutils.hanhu.getChildPointByHanHu
import mahjongutils.hanhu.getParentPointByHanHu

data class HanhuResult(
    val han: Int,
    val hu: Int,
    val parentPoint: Deferred<ParentPoint>,
    val childPoint: Deferred<ChildPoint>
)

class HanhuScreenModel : ScreenModel {
    var han: String by mutableStateOf("")
    var hu: String by mutableStateOf("")

    var hanErr: StringResource? by mutableStateOf(null)
    var huErr: StringResource? by mutableStateOf(null)

    var result: HanhuResult? by mutableStateOf(null)

    fun onSubmit() {
        screenModelScope.launch {
            val hanNum = han.toIntOrNull()
            val huNum = hu.toIntOrNull()
            var ok = true

            if (hanNum == null) {
                hanErr = MR.strings.text_invalid_han_number
                ok = false
            }

            if (huNum == null || huNum <= 0 || huNum % 10 != 0) {
                huErr = MR.strings.text_invalid_hu_number
                ok = false
            } else if (huNum > 140) {
                huErr = MR.strings.text_hu_exceeded_maximum
                ok = false
            }

            result = if (ok) {
                HanhuResult(
                    hanNum!!, huNum!!,
                    screenModelScope.async(Dispatchers.Default) {
                        getParentPointByHanHu(hanNum, huNum)
                    },
                    screenModelScope.async(Dispatchers.Default) {
                        getChildPointByHanHu(hanNum, huNum)
                    }
                )
            } else {
                null
            }
        }
    }
}