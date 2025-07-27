package io.ssttkkl.mahjongdetector


internal fun floatToFp16(input: Float): Short {
    var bits: Int = input.toBits()
    val F32_INFINITY: Int = Float.POSITIVE_INFINITY.toBits()
    val F16_MAX = 1199570944
    val DENORM_MAGIC = 1056964608
    val SIGN_MASK = Int.MIN_VALUE
    val ROUNDING_CONST = -939520001
    val sign = bits and Int.MIN_VALUE
    bits = bits xor sign
    var output: Short
    if (bits >= 1199570944) {
        output = (if (bits > F32_INFINITY) 32256 else 31744).toShort()
    } else if (bits < 947912704) {
        val tmp: Float =
            Float.fromBits(bits) + Float.fromBits(1056964608)
        output = (tmp.toBits() - 1056964608).toShort()
    } else {
        val mant_odd = bits shr 13 and 1
        bits -= 939520001
        bits += mant_odd
        output = (bits shr 13).toShort()
    }

    output = (output.toInt() or (sign shr 16).toShort().toInt()).toShort()
    return output
}

internal fun fp16ToFloat(input: Short): Float {
    val MAGIC = 947912704
    val SHIFTED_EXP = 260046848
    var bits = (input.toInt() and 32767) shl 13
    val exp = 260046848 and bits
    bits += 939524096
    if (exp == 260046848) {
        bits += 939524096
    } else if (exp == 0) {
        bits += 8388608
        val tmp: Float =
            Float.fromBits(bits) - Float.fromBits(947912704)
        bits = tmp.toBits()
    }

    bits = bits or ((input.toInt() and '耀'.code) shl 16)
    return Float.fromBits(bits)
}