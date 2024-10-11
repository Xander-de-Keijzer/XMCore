package nl.xandermarc.mc.lib.logging

import nl.xandermarc.mc.Main
import java.util.logging.Level
import kotlin.collections.last

//import kotlinx.datetime.Clock
//import kotlinx.datetime.Instant
//import kotlinx.datetime.format

private val lambdaRegex = "\\\$lambda\\\$\\d*".toRegex()
private val jarRegex = ".*\\.jar//".toRegex()

fun trace(): String {
    val full = Exception().stackTrace.map {
        it.toString()
    }.first {
        !it.contains("nl.xandermarc.mc.lib.logging")
    }.replace(
        lambdaRegex, ""
    ).replace(
        jarRegex, ""
    )
    if (LogSettings.level != LogPriority.DEBUG) return "(" + full.split("(").last()
    return full
}

fun <T> T.log(priority: LogPriority, message: String): T {//, timestamp: Instant = Clock.System.now()): T {
    if (LogSettings.shouldLog(priority)) {
        when(priority) {
            LogPriority.DEBUG -> Main.instance.logger.log(Level.INFO, "${trace()}: $message")
            LogPriority.INFO -> Main.instance.logger.log(Level.INFO, "${trace()}: $message")
            LogPriority.WARN -> Main.instance.logger.log(Level.WARNING, "${trace()}: $message")
            LogPriority.ERROR -> Main.instance.logger.log(Level.SEVERE, "${trace()}: $message")
        }
    }
    return this
}
fun <T> T.debug(message: String): T = log(LogPriority.DEBUG, message)
fun <T> T.info(message: String): T = log(LogPriority.INFO, message)
fun <T> T.warn(message: String): T = log(LogPriority.WARN, message)
fun <T> T.error(message: String): T = log(LogPriority.ERROR, message)
inline fun <T> T.debug(message: T.() -> String): T = debug(message())
inline fun <T> T.info(message: T.() -> String): T = info(message())
inline fun <T> T.warn(message: T.() -> String): T = warn(message())
inline fun <T> T.error(message: T.() -> String): T = error(message())
inline fun <T> Iterable<T>.debugAll(message: T.() -> String): Iterable<T> = onEach { debug(message(it)) }
inline fun <T> Iterable<T>.infoAll(message: T.() -> String): Iterable<T> = onEach { info(message(it)) }
inline fun <T> Iterable<T>.warnAll(message: T.() -> String): Iterable<T> = onEach { warn(message(it)) }
inline fun <T> Iterable<T>.errorAll(message: T.() -> String): Iterable<T> = onEach { error(message(it)) }
