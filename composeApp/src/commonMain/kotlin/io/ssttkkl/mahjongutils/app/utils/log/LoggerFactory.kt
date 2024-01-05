package io.ssttkkl.mahjongutils.app.utils.log

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.concurrent.Volatile

object LoggerFactory : ILoggerFactory {
    @Volatile
    private var _impl: ILoggerFactory? = null

    var impl: ILoggerFactory
        get() = checkNotNull(_impl)
        set(value) {
            if (_impl != null) {
                error("impl already set")
            }

            _impl = value
            // 设置此前创建的MDDLoggerDelegate
            memoLock.withLock {
                pendingDelegateLogger.forEach { it.delegate = impl.getLogger(it.tag) }
                pendingDelegateLogger.clear()
            }
        }

    // 保证同一个tag只会调用一次impl.getLogger
    private val memo = hashMapOf<String, Logger>()
    private val memoLock = reentrantLock()

    // 记录等实现注册进来后再设置的LoggerDelegate
    private var pendingDelegateLogger = arrayListOf<LoggerDelegate>()

    override fun getLogger(tag: String): Logger = memoLock.withLock {
        memo[tag] ?: run {
            // 若实现未注册，返回代理，使得壳注册实现前获取的logger也能正常工作
            val logger = _impl?.getLogger(tag)
                ?: LoggerDelegate(tag).also { pendingDelegateLogger.add(it) }

            memo[tag] = logger
            logger
        }
    }
}