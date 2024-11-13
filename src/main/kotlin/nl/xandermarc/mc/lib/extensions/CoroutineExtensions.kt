@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import kotlinx.coroutines.*
import nl.xandermarc.mc.lib.data.Globals.asyncScope
import nl.xandermarc.mc.lib.data.Globals.instance
import nl.xandermarc.mc.lib.data.Globals.logger
import nl.xandermarc.mc.lib.data.Globals.server
import nl.xandermarc.mc.lib.data.Globals.supervisor
import nl.xandermarc.mc.lib.data.Globals.syncScope
import java.util.concurrent.Executor
import java.util.logging.Level
import kotlin.reflect.jvm.jvmName

/*
    Scope Utils
 */

inline val syncExecutor get() = Executor { task ->
    if (server.pluginManager.isPluginEnabled(instance) && supervisor.isActive) {
        server.globalRegionScheduler.run(instance) { task.run() }
    } else {
        logger.severe("Attempting to start a synchronous task while plugin is disabled " +
                "or syncJob is cancelled, running task directly instead.")
        task.run()
    }
}

fun createExceptionHandler(name: String) = CoroutineExceptionHandler { _, throwable ->
    when (throwable) {
        is CancellationException -> logger.info("Job($name) was cancelled.")
        else -> logger.log(Level.SEVERE, "Job($name) failed with exception", throwable)
    }
}

/*
    Job Creation
 */

fun Any.launchSync(
    name: String = this::class.run { simpleName ?: jvmName },
    block: suspend CoroutineScope.() -> Unit
): Job = syncScope.launchJob("SyncJob($name)", block)

fun Any.launchAsync(
    name: String = this::class.run { simpleName ?: jvmName },
    block: suspend CoroutineScope.() -> Unit
): Job = asyncScope.launchJob("AsyncJob($name)", block)

private fun CoroutineScope.launchJob(
    name: String,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(createExceptionHandler(name)) {
    ensureActive()
    block()
    debug { "Job($name) has been completed successfully." }
}

/*
    Job Completion
 */

fun Job.completeAll() = runBlocking {
    val supervisor = this@completeAll
    if (!supervisor.isActive) return@runBlocking

    try {
        withTimeout(10000L) {
            info { "Awaiting coroutine jobs..." }
            supervisor.children.forEach { job ->
                job.join() // Await completion of each child job under supervisor
            }
            supervisor.cancelAndJoin() // Finally, cancel supervisor itself
        }
        info { "All jobs successfully completed." }
    } catch (e: TimeoutCancellationException) {
        error { "Not all coroutines jobs completed in 10 seconds, potential side-effects may occur." }
        try {
            warn { "Cancelling SupervisorJob which will cancel all child jobs..." }
            withTimeout(5000L) {
                // If not all child jobs were completed cancel supervisor,
                // which will cancel all children as well
                supervisor.cancelAndJoin()
            }
        } catch (e: TimeoutCancellationException) {
            error { "SupervisorJob (and child jobs) failed to cancel in 5 seconds." }
            // As a last effort, cancel and await each child individually
            withTimeout(5000L) {
                supervisor.children.forEach { job ->
                    job.cancelAndJoin()
                }
            }
        }
    }
}
