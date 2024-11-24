@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.lib.data.Globals
import java.util.logging.Level

fun isLoggingClass(name: String): Boolean {
    return name.lowercase().contains("log")
}

fun <T> T.log(level: Level, message: String, allowFullTrace: Boolean = false): T {
    val full = Exception().stackTrace
        .map { it.toString() }
        .first { !isLoggingClass(it) }
        .replace("\\\$lambda\\\$\\d*".toRegex(), "")
        .replace(".*\\.jar//".toRegex(), "")

    if (level == Level.INFO || !allowFullTrace) {
        val stripped = "(" + full.split("(").last()
        Globals.logger.log(level, "$stripped: $message")
    } else {
        Globals.logger.log(level, "$full: $message")
    }
    return this
}

inline fun <T> T.debug(message: T.() -> String): T = log(Level.FINE, message())
inline fun <T> T.info(message: T.() -> String): T = log(Level.INFO, message())
inline fun <T> T.warn(message: T.() -> String): T = log(Level.WARNING, message())
inline fun <T> T.error(message: T.() -> String): T = log(Level.SEVERE, message())
inline fun <T> Iterable<T>.debugAll(message: T.() -> String): Iterable<T> = onEach { debug { message(it) } }
inline fun <T> Iterable<T>.infoAll(message: T.() -> String): Iterable<T> = onEach { info { message(it) } }
inline fun <T> Iterable<T>.warnAll(message: T.() -> String): Iterable<T> = onEach { warn { message(it) } }
inline fun <T> Iterable<T>.errorAll(message: T.() -> String): Iterable<T> = onEach { error { message(it) } }
