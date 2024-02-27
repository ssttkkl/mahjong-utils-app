package io.ssttkkl.mahjongutils.app.screens.hanhu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.models.AppOptions
import io.ssttkkl.mahjongutils.app.models.hanhu.HanHuArgs
import io.ssttkkl.mahjongutils.app.screens.base.FormState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_hu_exceeded_maximum
import mahjongutils.composeapp.generated.resources.text_invalid_han_number
import mahjongutils.composeapp.generated.resources.text_invalid_hu_number
import mahjongutils.hanhu.HanHuOptions
import org.jetbrains.compose.resources.StringResource

class HanhuFormState(
    val scope: CoroutineScope
) : FormState<HanHuArgs> {
    var han: String by mutableStateOf("")
    var hu: String by mutableStateOf("")

    private var hanhuOptionsState = mutableStateOf(HanHuOptions.Default)
    var hanhuOptions: HanHuOptions
        get() = hanhuOptionsState.value
        set(value) {
            hanhuOptionsState.value = value
            scope.launch {
                AppOptions.datastore.updateData {
                    it.copy(hanHuOptions = value)
                }
            }
        }

    var hanErr: StringResource? by mutableStateOf(null)
    var huErr: StringResource? by mutableStateOf(null)

    init {
        scope.launch {
            AppOptions.datastore.data.collectLatest {
                hanhuOptionsState.value = it.hanHuOptions
            }
        }
    }


    override fun resetForm() {
        han = ""
        hu = ""

        hanErr = null
        huErr = null
    }

    override fun fillFormWithArgs(args: HanHuArgs, check: Boolean) {
        han = args.han.toString()
        hu = args.hu.toString()
        if (check) {
            onCheck()
        }
    }

    override fun onCheck(): HanHuArgs? {
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
            return HanHuArgs(hanNum!!, huNum!!, hanhuOptions)
        } else {
            return null
        }
    }
}