package io.ssttkkl.mahjongutils.app.utils

import java.util.UUID

actual fun createRandomUUID(): Long {
    return UUID.randomUUID().mostSignificantBits
}