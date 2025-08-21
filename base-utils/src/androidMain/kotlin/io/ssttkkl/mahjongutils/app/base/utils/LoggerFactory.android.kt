package io.ssttkkl.mahjongutils.app.base.utils

import android.util.Log
import java.util.WeakHashMap
import kotlin.reflect.KClass

actual object LoggerFactory {
    private val cache = WeakHashMap<String, Logger>()

    @Synchronized
    actual fun getLogger(tag: String): Logger {
        return cache.getOrPut(tag) {
            object : Logger {
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
    }

    actual fun getLogger(clazz: KClass<*>): Logger {
        return getLogger(clazz.simpleName ?: "")
    }
}