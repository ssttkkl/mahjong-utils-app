package io.ssttkkl.mahjongutils.app.base.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.Encoder
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

actual object LoggerFactory {
    private fun configureLogback() {
        val ctx = LoggerFactory.getILoggerFactory() as LoggerContext
        ctx.reset() // 清除默认配置

        // 1. 控制台Appender (DEBUG及以上)
        val consoleAppender = ConsoleAppender<Any>().apply {
            name = "CONSOLE"
            context = ctx
            encoder = PatternLayoutEncoder().apply {
                context = ctx
                pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
                start()
            } as Encoder<Any>
            addFilter(ThresholdFilter().apply {
                setLevel(Level.DEBUG.levelStr)
                start()
            } as Filter<Any>)
            start()
        }

        // 2. 滚动文件Appender (INFO及以上)
        val logsDir = FileUtils.sandboxPath.toString() + "/logs"
        val rollingPolicy = TimeBasedRollingPolicy<Any>().apply {
            context = ctx
            fileNamePattern = "$logsDir/app.%d{yyyy-MM-dd}.%i.log"
            setParent(RollingFileAppender<Any>().apply {
                context = ctx
                name = "FILE"
                file = "$logsDir/app.log"
            })
            maxHistory = 7
            timeBasedFileNamingAndTriggeringPolicy = SizeAndTimeBasedFNATP<Any>().apply {
                setMaxFileSize(FileSize.valueOf("10MB"))
            }
            start()
        }

        val fileAppender = RollingFileAppender<Any>().apply {
            name = "FILE"
            context = ctx
            file = "$logsDir/app.log"
            encoder = PatternLayoutEncoder().apply {
                context = ctx
                pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
                start()
            } as Encoder<Any>
            setRollingPolicy(rollingPolicy)
            addFilter(ThresholdFilter().apply {
                setLevel(Level.INFO.levelStr)
                start()
            } as Filter<Any>)
            start()
        }

        // 3. 配置根Logger
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).apply {
            level = Level.DEBUG
            addAppender(consoleAppender as Appender<ILoggingEvent>)
            addAppender(fileAppender as Appender<ILoggingEvent>)
        }
    }

    init {
        configureLogback()
    }

    actual fun getLogger(tag: String): io.ssttkkl.mahjongutils.app.base.utils.Logger {
        return object : io.ssttkkl.mahjongutils.app.base.utils.Logger {
            val innerLogger = LoggerFactory.getLogger(tag)
            override fun trace(msg: String) {
                innerLogger.trace(msg)
            }

            override fun debug(msg: String) {
                innerLogger.debug(msg)
            }

            override fun info(msg: String) {
                innerLogger.info(msg)
            }

            override fun warn(msg: String) {
                innerLogger.warn(msg)
            }

            override fun error(throwable: Throwable) {
                innerLogger.error("", throwable)
            }

            override fun error(msg: String, throwable: Throwable?) {
                innerLogger.error(msg, throwable)
            }
        }
    }

    actual fun getLogger(clazz: KClass<*>): io.ssttkkl.mahjongutils.app.base.utils.Logger {
        return getLogger(clazz.simpleName ?: "")
    }
}