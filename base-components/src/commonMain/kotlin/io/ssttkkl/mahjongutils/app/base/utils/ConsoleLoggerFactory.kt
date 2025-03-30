package io.ssttkkl.mahjongutils.app.base.utils

import kotlin.reflect.KClass

object ConsoleLoggerFactory {
    fun getLogger(tag: String): Logger {
        return object : Logger {
            override fun trace(msg: String) {
                println("TRACE|[${tag}]${msg}")
            }

            override fun debug(msg: String) {
                println("DEBUG|[${tag}]${msg}")
            }

            override fun info(msg: String) {
                println(" INFO|[${tag}]${msg}")
            }

            override fun warn(msg: String) {
                println(" WARN|[${tag}]${msg}")
            }

            override fun error(throwable: Throwable) {
                println("ERROR|[${tag}]\n${throwable.stackTraceToString()}")
            }

            override fun error(msg: String, throwable: Throwable?) {
                println("ERROR|[${tag}]${msg}\n${throwable?.stackTraceToString() ?: ""}")
            }
        }
    }

    fun getLogger(clazz: KClass<*>): Logger {
        return getLogger(clazz.simpleName ?: "")
    }
}
