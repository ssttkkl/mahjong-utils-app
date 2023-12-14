package io.ssttkkl.mahjongutils.app.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFUUIDCreate
import platform.CoreFoundation.CFUUIDGetUUIDBytes

@OptIn(ExperimentalForeignApi::class)
actual fun createRandomUUID(): Long {
    val uuid = CFUUIDCreate(null)
    val bytes = CFUUIDGetUUIDBytes(uuid)

    var mostSignificantBits = 0L
    bytes.useContents {
        mostSignificantBits = mostSignificantBits or byte0.toLong() shl 56
        mostSignificantBits = mostSignificantBits or byte1.toLong() shl 48
        mostSignificantBits = mostSignificantBits or byte2.toLong() shl 40
        mostSignificantBits = mostSignificantBits or byte3.toLong() shl 32
        mostSignificantBits = mostSignificantBits or byte4.toLong() shl 24
        mostSignificantBits = mostSignificantBits or byte5.toLong() shl 16
        mostSignificantBits = mostSignificantBits or byte6.toLong() shl 8
        mostSignificantBits = mostSignificantBits or byte7.toLong()
    }

    CFRelease(uuid)
    return mostSignificantBits
}