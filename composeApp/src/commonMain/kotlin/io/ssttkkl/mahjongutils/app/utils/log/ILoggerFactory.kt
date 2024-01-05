package io.ssttkkl.mahjongutils.app.utils.log

import kotlin.reflect.KClass

interface ILoggerFactory {
    fun getLogger(tag: String): Logger
    fun getLogger(clazz: KClass<*>): Logger {
        return getLogger(clazz.simpleName ?: "")
    }
}