package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.models.AppOptions
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.hanhu.ChildPoint
import mahjongutils.hanhu.HanHuOptions
import mahjongutils.hanhu.ParentPoint
import mahjongutils.hanhu.getChildPointByHanHu
import mahjongutils.hanhu.getParentPointByHanHu
import org.jetbrains.compose.resources.StringResource

data class HanhuResult(
    val han: Int,
    val hu: Int,
    val parentPoint: Deferred<ParentPoint>,
    val childPoint: Deferred<ChildPoint>
)

class HanhuScreenModel : ScreenModel {
    var han: String by mutableStateOf("")
    var hu: String by mutableStateOf("")

    private var hanhuOptionsState = mutableStateOf(HanHuOptions.Default)
    var hanhuOptions: HanHuOptions
        get() = hanhuOptionsState.value
        set(value) {
            hanhuOptionsState.value = value
            screenModelScope.launch {
                AppOptions.datastore.updateData {
                    it.copy(hanHuOptions = value)
                }
            }
        }

    init {
        screenModelScope.launch {
            AppOptions.datastore.data.collectLatest {
                hanhuOptionsState.value = it.hanHuOptions
            }
        }
    }

    var hanErr: StringResource? by mutableStateOf(null)
    var huErr: StringResource? by mutableStateOf(null)

    var result: HanhuResult? by mutableStateOf(null)

    fun onSubmit() {
        screenModelScope.launch {
            val hanNum = han.toIntOrNull()
            val huNum = hu.toIntOrNull()
            var ok = true

            if (hanNum == null) {
                hanErr = Res.string.text_invalid_han_number
                ok = false
            } else {
                hanErr = null
            }

            if (huNum == null || huNum <= 0 || huNum % 10 != 0 && huNum != 25) {
                huErr = Res.string.text_invalid_hu_number
                ok = false
            } else if (huNum > 140) {
                huErr = Res.string.text_hu_exceeded_maximum
                ok = false
            } else {
                huErr = null
            }

            if (ok) {
                if (result?.han != hanNum!! || result?.hu != huNum!!) {
                    result = HanhuResult(
                        hanNum, huNum!!,
                        screenModelScope.async(Dispatchers.Default) {
                            getParentPointByHanHu(hanNum, huNum, hanhuOptions)
                        },
                        screenModelScope.async(Dispatchers.Default) {
                            getChildPointByHanHu(hanNum, huNum, hanhuOptions)
                        }
                    )
                }
            } else {
                result = null
            }
        }
    }
}