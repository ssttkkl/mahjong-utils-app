package io.ssttkkl.mahjongutils.app.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle

actual fun LocalDateTime.localizedFormatting(): String {
    val formatter = NSDateFormatter().apply {
        dateStyle = NSDateFormatterMediumStyle
        timeStyle = NSDateFormatterMediumStyle
    }
    return formatter.stringFromDate(
        NSCalendar.currentCalendar.dateFromComponents(this.toNSDateComponents())!!
    )
}