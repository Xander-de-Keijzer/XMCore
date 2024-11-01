package nl.xandermarc.mc.lib.extensions

import kotlinx.coroutines.*
import nl.xandermarc.mc.lib.data.Globals.instance
import nl.xandermarc.mc.lib.data.Globals.jobs
import nl.xandermarc.mc.lib.data.Globals.logger
import nl.xandermarc.mc.lib.data.Globals.readScope
import nl.xandermarc.mc.lib.data.Globals.server
import nl.xandermarc.mc.lib.data.Globals.supervisor
import nl.xandermarc.mc.lib.data.Globals.syncScope
import nl.xandermarc.mc.lib.data.Globals.writeScope
import java.util.concurrent.Executor

@OptIn(ExperimentalStdlibApi::class)
val Job.hexKey: String get() = hashCode().toHexString()

fun syncDispatcher() = Executor { task ->
    if (server.pluginManager.isPluginEnabled(instance) && supervisor.isActive) {
        server.globalRegionScheduler.run(instance) { task.run() }
    } else {
        logger.error("Attempting to start a synchronous task while plugin is disabled " +
                "or syncJob is cancelled, running task directly instead.")
        task.run()
    }
}.asCoroutineDispatcher()

fun launchSyncJob(
    name: String = "Unknown",
    block: suspend CoroutineScope.() -> Unit
): Job = syncScope.launchJob("SyncJob($name)", block)

fun launchReadJob(
    name: String = "Unknown",
    block: suspend CoroutineScope.() -> Unit
): Job = readScope.launchJob("ReadJob($name)", block)

fun launchWriteJob(
    name: String = "Unknown",
    block: suspend CoroutineScope.() -> Unit
): Job = writeScope.launchJob("WriteJob($name)", block)

fun Job.complete() = runBlocking {
    if (!this@complete.isActive) return@runBlocking

    try {
        withTimeout(5000L) {
            info("Awaiting coroutine jobs...")
            this@complete.children.forEach { job ->
                job.cancelAndJoin() // Cancel each child job under supervisor
            }
            this@complete.cancelAndJoin() // Finally, cancel supervisor itself
        }
        info("All jobs successfully cancelled.")
    } catch (e: TimeoutCancellationException) {
        error("Not all coroutines jobs cancelled in 5 seconds, cancelling supervisor...")
        try {
            this@complete.cancelAndJoin()
        } catch (e: TimeoutCancellationException) {
            error("Coroutines could not be cancelled, potential side-effects may occur.")
            jobs.keys.forEach { job ->
                job.cancelAndJoin()
            }
        }
    }
}

fun Job?.joinAndLaunchReadJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    launchReadJob(name) {
        val currentJob = this@joinAndLaunchReadJob
        if (currentJob != null && !currentJob.isCompleted) {
            ensureActive()
            debug("Job($name) waiting on previous job ${jobs[currentJob]} to complete...")
            currentJob.join()
        }
        ensureActive()
        block()
    }

private fun CoroutineScope.launchJob(
    name: String = "Unknown",
    block: suspend CoroutineScope.() -> Unit
): Job = this.launch {
    ensureActive()
    block()
}.also { job ->
    val jobKey = "Job($name)#${job.hexKey}"
    jobs[job] = jobKey
    job.invokeOnCompletion { throwable ->
        when (throwable) {
            null -> debug("$jobKey has been completed successfully.")
            is CancellationException -> debug("$jobKey has been cancelled.")
            else -> error("$jobKey has failed with exception $throwable")
        }
        jobs.remove(job)
    }
}
