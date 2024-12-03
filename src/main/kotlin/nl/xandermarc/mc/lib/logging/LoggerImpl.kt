package nl.xandermarc.mc.lib.logging

import nl.xandermarc.mc.lib.logging.Logging.log

class LoggerImpl internal constructor(
    name: String? = null,
    level: Level? = null,
): Logger {
    override var logLevel = level
    override var logName: String = (name ?: this::class.java.packageName).lowercase()

    override fun trace(throwable: Throwable?, message: () -> Any) =
        log(Level.TRACE, message, throwable)
    override fun debug(throwable: Throwable?, message: () -> Any) =
        log(Level.DEBUG, message, throwable)
    override fun info(throwable: Throwable?, message: () -> Any) =
        log(Level.INFO, message, throwable)
    override fun warn(throwable: Throwable?, message: () -> Any) =
        log(Level.WARN, message, throwable)
    override fun error(throwable: Throwable?, message: () -> Any) =
        log(Level.ERROR, message, throwable)
}
