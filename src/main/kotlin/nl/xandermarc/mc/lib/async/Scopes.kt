package nl.xandermarc.mc.lib.async

import nl.xandermarc.mc.Main
import java.util.concurrent.TimeUnit

private val asyncScheduler by lazy { Main.instance.server.asyncScheduler }
private val syncScheduler by lazy { Main.instance.server.globalRegionScheduler }

fun sync(block: () -> Unit) =
    syncScheduler.run(Main.instance) {
        block()
    }

fun sync(delay: Long, block: () -> Unit) =
    syncScheduler.runDelayed(
        Main.instance,
        { block() },
        delay
    )

fun async(block: () -> Unit, complete: () -> Unit) =
    async {
        block()
        sync {
            complete()
        }
    }

fun async(block: () -> Unit) =
    asyncScheduler.runNow(Main.instance) {
        block()
    }

fun async(delay: Long, block: () -> Unit) =
    asyncScheduler.runDelayed(
        Main.instance,
        { block() },
        delay,
        TimeUnit.MICROSECONDS
    )
