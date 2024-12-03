@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.lib.data.Globals.logger

@Deprecated("Please extend Logger class or use Globals.logger directly",
    ReplaceWith("Globals.logger.debug", "nl.xandermarc.mc.lib.logging.Logger")
)
inline fun <T> T.debug(crossinline message: T.() -> String): T = apply { logger.debug { message(this) } }
inline fun <T> T.info(crossinline message: T.() -> String): T = apply { logger.info { message(this) } }
inline fun <T> T.warn(crossinline message: T.() -> String): T = apply { logger.warn { message(this) } }
inline fun <T> T.error(crossinline message: T.() -> String): T = apply { logger.error { message(this) } }
inline fun <T> Iterable<T>.debugAll(crossinline message: T.() -> String): Iterable<T> = onEach { debug { message(it) } }
inline fun <T> Iterable<T>.infoAll(crossinline message: T.() -> String): Iterable<T> = onEach { info { message(it) } }
inline fun <T> Iterable<T>.warnAll(crossinline message: T.() -> String): Iterable<T> = onEach { warn { message(it) } }
inline fun <T> Iterable<T>.errorAll(crossinline message: T.() -> String): Iterable<T> = onEach { error { message(it) } }
