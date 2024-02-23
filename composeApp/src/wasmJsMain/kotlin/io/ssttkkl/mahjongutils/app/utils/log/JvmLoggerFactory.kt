package io.ssttkkl.mahjongutils.app.utils.log


object JvmLoggerFactory : ILoggerFactory {
    override fun getLogger(tag: String): Logger {
        return object : Logger {
            override fun trace(msg: String) {
                println(String.format("TRACE|[%s]%s", tag, msg))
            }

            override fun debug(msg: String) {
                println(String.format("DEBUG|[%s]%s", tag, msg))
            }

            override fun info(msg: String) {
                println(String.format(" INFO|[%s]%s", tag, msg))
            }

            override fun warn(msg: String) {
                println(String.format(" WARN|[%s]%s", tag, msg))
            }

            override fun error(throwable: Throwable) {
                println(String.format("ERROR|[%s]\n%s", tag, throwable.stackTraceToString()))
            }

            override fun error(msg: String, throwable: Throwable?) {
                println(String.format("ERROR|[%s]%s\n%s", tag, msg, throwable?.stackTraceToString() ?: ""))
            }

        }
    }
}
