package io.ssttkkl.mahjongutils.app.base.utils

import android.os.Build
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

actual fun LocalDateTime.localizedFormatting(): String {
    if (Build.VERSION.SDK_INT >= 26) {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        return this.toJavaLocalDateTime().format(formatter)
    } else {
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val date = Date(toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds())
        return formatter.format(date)
    }
}