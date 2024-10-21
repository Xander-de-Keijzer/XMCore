package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.core.XMC
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


@Suppress("UnstableApiUsage")
val JavaPlugin.pluginName get() = pluginMeta.displayName

fun <T> T.log(level: Level, message: String): T {
    val full = Exception().stackTrace
        .map { it.toString() }
        .first { !it.contains("nl.xandermarc.mc.lib.extensions.LoggingExtensions") }
        .replace("\\\$lambda\\\$\\d*".toRegex(), "")
        .replace(".*\\.jar//".toRegex(), "")

    if (level == Level.INFO) {
        val stripped = "(" + full.split("(").last()
        XMC.logger.info( "$stripped: $message")
    } else {
        XMC.logger.log(level, "$full: $message")
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
