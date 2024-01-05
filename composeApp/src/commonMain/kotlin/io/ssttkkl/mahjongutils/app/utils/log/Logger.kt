package io.ssttkkl.mahjongutils.app.utils.log

interface Logger {
    fun trace(msg: String)
    fun debug(msg: String)
    fun info(msg: String)
    fun warn(msg: String)
    fun error(throwable: Throwable)
    fun error(msg: String, throwable: Throwable? = null)
}

