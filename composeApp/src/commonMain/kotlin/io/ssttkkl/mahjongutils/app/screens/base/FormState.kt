package io.ssttkkl.mahjongutils.app.screens.base

interface FormState<ARG> {
    fun fillFormWithArgs(args: ARG, check: Boolean = true)
    fun resetForm()
    fun onCheck(): ARG?

    fun extractToMap(): Map<String, String>
    fun applyFromMap(map: Map<String, String>)
}