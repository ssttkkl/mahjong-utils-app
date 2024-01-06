package io.ssttkkl.mahjongutils.app.utils.log

internal class LoggerDelegate(
    val tag: String
) : Logger {

    var delegate: Logger? = null

    override fun trace(msg: String) {
        delegate?.trace(msg)
    }

    override fun debug(msg: String) {
        delegate?.debug(msg)
    }

    override fun info(msg: String) {
        delegate?.info(msg)
    }

    override fun warn(msg: String) {
        delegate?.warn(msg)
    }

    override fun error(throwable: Throwable) {
        delegate?.error(throwable)
    }

    override fun error(msg: String, throwable: Throwable?) {
        delegate?.error(msg, throwable)
    }

}