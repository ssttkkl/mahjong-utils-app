package io.ssttkkl.mahjongutils.app.base.utils

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass

fun KotlinLogging.logger(clazz: KClass<*>): KLogger = logger(clazz.simpleName ?: "<anonymous>")
