package io.ssttkkl.mahjongutils.app.base.utils

import kotlin.reflect.KClass

expect object LoggerFactory {
    fun getLogger(tag: String): Logger
    fun getLogger(clazz: KClass<*>): Logger
}

interface Logger {
    fun trace(msg: String)
    fun debug(msg: String)
    fun info(msg: String)
    fun warn(msg: String)
    fun error(throwable: Throwable)
    fun error(msg: String, throwable: Throwable? = null)
}

