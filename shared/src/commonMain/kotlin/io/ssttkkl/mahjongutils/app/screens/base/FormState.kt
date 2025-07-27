package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Stable

@Stable
interface FormState<ARG> {
    fun fillFormWithArgs(args: ARG, check: Boolean = true)
    fun resetForm()
    fun onCheck(): ARG?
    fun applyFromMap(map: Map<String, String>)
}