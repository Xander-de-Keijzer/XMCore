package nl.xandermarc.mc.lib.async

import kotlinx.coroutines.*
import nl.xandermarc.mc.Main

private val syncScheduler by lazy { Main.instance.server.globalRegionScheduler }

fun sync(block: () -> Unit) =
    syncScheduler.run(Main.instance) {
        block()
    }

fun <T> async(block: CoroutineScope.() -> T) = runBlocking {
    async(Dispatchers.IO, block=block)
}
