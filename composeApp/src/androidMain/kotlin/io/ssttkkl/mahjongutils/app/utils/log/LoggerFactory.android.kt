package io.ssttkkl.mahjongutils.app.utils.log

import android.util.Log
import kotlin.reflect.KClass

actual object LoggerFactory {
    actual fun getLogger(tag: String): Logger {
        return object : Logger {
            override fun trace(msg: String) {
                Log.v(tag, msg)
            }

            override fun debug(msg: String) {
                Log.d(tag, msg)
            }

            override fun info(msg: String) {
                Log.i(tag, msg)
            }

            override fun warn(msg: String) {
                Log.w(tag, msg)
            }

            override fun error(throwable: Throwable) {
                Log.e(tag, "", throwable)
            }

            override fun error(msg: String, throwable: Throwable?) {
                Log.e(tag, msg, throwable)
            }

        }
    }

    actual fun getLogger(clazz: KClass<*>): Logger {
        return getLogger(clazz.simpleName ?: "")
    }
}