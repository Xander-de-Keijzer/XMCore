package nl.xandermarc.mc.lib.logging

//import kotlinx.datetime.Clock
//import kotlinx.datetime.Instant
//import kotlinx.datetime.format

private val lambdaStr = "\\\$lambda\\\$\\d*".toRegex()

fun <T> T.log(priority: LogPriority, message: String, delegated: Int = 2): T {//, timestamp: Instant = Clock.System.now()): T {
    if (LogSettings.shouldLog(priority)) {
        println("${lambdaStr.replace(Exception().stackTrace[delegated].toString(), "")}) [$priority]: $message")
    }
    return this
}
fun <T> T.debug(message: String, delegated: Int = 4): T = log(LogPriority.DEBUG, message, delegated)
fun <T> T.info(message: String, delegated: Int = 4): T = log(LogPriority.INFO, message, delegated)
fun <T> T.warn(message: String, delegated: Int = 4): T = log(LogPriority.WARN, message, delegated)
fun <T> T.error(message: String, delegated: Int = 4): T = log(LogPriority.ERROR, message, delegated)
inline fun <T> T.debug(message: T.() -> String): T = debug(message(), 3)
inline fun <T> T.info(message: T.() -> String): T = info(message(), 3)
inline fun <T> T.warn(message: T.() -> String): T = warn(message(), 3)
inline fun <T> T.error(message: T.() -> String): T = error(message(), 3)
fun <T, C : Iterable<T>> C.debugAll(message: T.() -> String): C = onEach { debug(message(it), 4) }
fun <T, C : Iterable<T>> C.infoAll(message: T.() -> String): C = onEach { info(message(it), 4) }
fun <T, C : Iterable<T>> C.warnAll(message: T.() -> String): C = onEach { warn(message(it), 4) }
fun <T, C : Iterable<T>> C.errorAll(message: T.() -> String): C = onEach { error(message(it), 4) }
