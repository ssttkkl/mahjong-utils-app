package io.ssttkkl.mahjongdetector

import org.khronos.webgl.Uint8ClampedArray

// JS: Uint8ClampedArray declaration unusable
// https://youtrack.jetbrains.com/issue/KT-24583/JS-Uint8ClampedArray-declaration-unusable
// 妈的排查了两天才发现这里有个大坑
private fun getMethodImplForUint8ClampedArray(obj: Uint8ClampedArray, index: Int): UByte {
    js("return obj[index];")
}

public operator fun Uint8ClampedArray.get(index: Int): UByte =
    getMethodImplForUint8ClampedArray(this, index)

private fun setMethodImplForUint8ClampedArray(obj: Uint8ClampedArray, index: Int, value: UByte) {
    js("obj[index] = value;")
}

public operator fun Uint8ClampedArray.set(index: Int, value: UByte) =
    setMethodImplForUint8ClampedArray(this, index, value)
