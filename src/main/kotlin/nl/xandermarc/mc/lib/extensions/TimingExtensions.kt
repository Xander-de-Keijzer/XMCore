@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.lib.data.Globals
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

data class PreviousHolder(
    var nano: Long
)

@OptIn(ExperimentalContracts::class)
inline fun measureMillis(block: (PreviousHolder) -> Unit): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val start = System.currentTimeMillis()
    block(PreviousHolder(System.nanoTime()))
    return System.currentTimeMillis() - start
}

fun Any.complete(previous: PreviousHolder, builder: (String) -> String) {
    val diff = System.nanoTime() - previous.nano
    if (diff > 1000000) Globals.logger.info(builder("%.3f ms".format(diff.toDouble()/1000000)))
    else if (diff > 1000) Globals.logger.info(builder("%.3f us".format(diff.toDouble()/1000)))
    else Globals.logger.info(builder("$diff ns"))
    previous.nano = System.nanoTime()
}

fun Any.enabled(previous: PreviousHolder) = complete(previous) { "${this::class.simpleName} enabled. ($it)" }
fun Any.registered(previous: PreviousHolder) = complete(previous) { "${this::class.simpleName} registered. ($it)" }
fun Any.disabled(previous: PreviousHolder) = complete(previous) { "${this::class.simpleName} disabled. ($it)" }
