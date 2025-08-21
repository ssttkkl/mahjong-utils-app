package io.ssttkkl.mahjongutils.app.base.utils

import kotlinx.datetime.LocalDateTime

private fun getDateTimeFormatOptions(): JsAny =
    js("({year:'numeric',month:'short',day:'numeric',hour:'2-digit',minute:'2-digit',second:'2-digit'})")

actual fun LocalDateTime.localizedFormatting(): String {
    val jsDate = Date(
        this.year,
        this.monthNumber - 1,
        this.dayOfMonth,
        this.hour,
        this.minute,
        this.second,
        0
    )
    return jsDate.toLocaleDateString(
        options = getDateTimeFormatOptions()
    )
}

// for interop use
private external class Date {
    constructor(
        year: Int,
        monthIndex: Int,
        day: Int,
        hours: Int,
        minutes: Int,
        seconds: Int,
        milliseconds: Int
    )

    fun toLocaleDateString(
        locales: String? = definedExternally,
        options: JsAny? = definedExternally
    ): String
}