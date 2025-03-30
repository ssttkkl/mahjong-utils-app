package io.ssttkkl.mahjongutils.app.base.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

expect fun LocalDateTime.localizedFormatting(): String

fun Instant.localizedFormatting(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    return toLocalDateTime(timeZone).localizedFormatting()
}