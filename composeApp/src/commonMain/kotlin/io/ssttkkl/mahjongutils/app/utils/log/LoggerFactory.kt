package io.ssttkkl.mahjongutils.app.utils.log

import kotlin.reflect.KClass

expect object LoggerFactory {
    fun getLogger(tag: String): Logger
    fun getLogger(clazz: KClass<*>): Logger
}