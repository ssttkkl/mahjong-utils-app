package io.ssttkkl.mahjongutils.app.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

actual fun LocalDateTime.localizedFormatting(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    return this.toJavaLocalDateTime().format(formatter)
}