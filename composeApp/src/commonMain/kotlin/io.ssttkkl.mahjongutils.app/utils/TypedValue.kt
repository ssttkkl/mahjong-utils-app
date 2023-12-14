package io.ssttkkl.mahjongutils.app.utils

import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class TypedValue(
    val value: Any?,
    val type: KType
) {
    inline fun <reified T> unwrap(): T = value as T

    companion object {
        inline fun <reified T> of(value: T) = TypedValue(value, typeOf<T>())
    }
}
