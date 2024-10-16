package nl.xandermarc.mc.lib.logging

import nl.xandermarc.mc.XMCPlugin
import java.util.logging.Level

private val lambdaRegex = "\\\$lambda\\\$\\d*".toRegex()
private val jarRegex = ".*\\.jar//".toRegex()

fun trace(level: Level): String {
    val full = Exception().stackTrace.map {
        it.toString()
    }.first {
        !it.contains("nl.xandermarc.mc.lib.logging")
    }.replace(
        lambdaRegex, ""
    ).replace(
        jarRegex, ""
    )
    if (level == Level.INFO) return "(" + full.split("(").last()
    return full
}

fun shouldLog(level: Level): Boolean {
    val levelValue = XMCPlugin.settings.logLevel.intValue()
    val checkValue = level.intValue()
    return (checkValue >= levelValue)
}

fun <T> T.log(priority: Level, message: String): T {//, timestamp: Instant = Clock.System.now()): T {
    if (shouldLog(priority)) {
        when (priority) {
            Level.FINE -> XMCPlugin.instance.logger.log(Level.INFO, "${trace(priority)}: $message")
            Level.INFO -> XMCPlugin.instance.logger.log(Level.INFO, "${trace(priority)}: $message")
            Level.WARNING -> XMCPlugin.instance.logger.log(Level.WARNING, "${trace(priority)}: $message")
            Level.SEVERE -> XMCPlugin.instance.logger.log(Level.SEVERE, "${trace(priority)}: $message")
        }
    }
    return this
}

fun <T> T.debug(message: String): T = log(Level.FINE, message)
fun <T> T.info(message: String): T = log(Level.INFO, message)
fun <T> T.warn(message: String): T = log(Level.WARNING, message)
fun <T> T.error(message: String): T = log(Level.SEVERE, message)
inline fun <T> T.debug(message: T.() -> String): T = debug(message())
inline fun <T> T.info(message: T.() -> String): T = info(message())
inline fun <T> T.warn(message: T.() -> String): T = warn(message())
inline fun <T> T.error(message: T.() -> String): T = error(message())
inline fun <T> Iterable<T>.debugAll(message: T.() -> String): Iterable<T> = onEach { debug(message(it)) }
inline fun <T> Iterable<T>.infoAll(message: T.() -> String): Iterable<T> = onEach { info(message(it)) }
inline fun <T> Iterable<T>.warnAll(message: T.() -> String): Iterable<T> = onEach { warn(message(it)) }
inline fun <T> Iterable<T>.errorAll(message: T.() -> String): Iterable<T> = onEach { error(message(it)) }
