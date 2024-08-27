package io.ssttkkl.mahjongutils.app.utils.log

import platform.Foundation.NSLog
import kotlin.reflect.KClass

actual object LoggerFactory {
    actual fun getLogger(tag: String): Logger {
        return object : Logger {
            override fun trace(msg: String) {
                NSLog("TRACE|[%s]%s", tag, msg)
            }

            override fun debug(msg: String) {
                NSLog("DEBUG|[%s]%s", tag, msg)
            }

            override fun info(msg: String) {
                NSLog(" INFO|[%s]%s", tag, msg)
            }

            override fun warn(msg: String) {
                NSLog(" WARN|[%s]%s", tag, msg)
            }

            override fun error(throwable: Throwable) {
                NSLog("ERROR|[%s]\n%s", tag, throwable.stackTraceToString())
            }

            override fun error(msg: String, throwable: Throwable?) {
                NSLog("ERROR|[%s]%s\n%s", tag, msg, throwable?.stackTraceToString() ?: "")
            }

        }
    }

    actual fun getLogger(clazz: KClass<*>): Logger {
        return getLogger(clazz.simpleName ?: "")
    }
}
