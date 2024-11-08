package nl.xandermarc.mc.lib.extensions

import kotlinx.coroutines.*
import nl.xandermarc.mc.lib.data.Globals.asyncScope
import nl.xandermarc.mc.lib.data.Globals.instance
import nl.xandermarc.mc.lib.data.Globals.jobs
import nl.xandermarc.mc.lib.data.Globals.logger
import nl.xandermarc.mc.lib.data.Globals.server
import nl.xandermarc.mc.lib.data.Globals.supervisor
import nl.xandermarc.mc.lib.data.Globals.syncScope
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

fun launchSync(
    name: String = "Unknown",
    block: suspend CoroutineScope.() -> Unit
): Job = syncScope.launchJob("SyncJob($name)", block)

fun launchAsync(
    name: String = "Unknown",
    block: suspend CoroutineScope.() -> Unit
): Job = asyncScope.launchJob("AsyncJob($name)", block)

fun Job.completeAll() = runBlocking {
    if (!this@completeAll.isActive) return@runBlocking

    try {
        withTimeout(5000L) {
            info("Awaiting coroutine jobs...")
            this@completeAll.children.forEach { job ->
                job.cancelAndJoin() // Cancel each child job under supervisor
            }
            this@completeAll.cancelAndJoin() // Finally, cancel supervisor itself
        }
        info("All jobs successfully cancelled.")
    } catch (e: TimeoutCancellationException) {
        error("Not all coroutines jobs cancelled in 5 seconds, cancelling supervisor...")
        try {
            this@completeAll.cancelAndJoin()
        } catch (e: TimeoutCancellationException) {
            error("Coroutines could not be cancelled, potential side-effects may occur.")
            jobs.keys.forEach { job ->
                job.cancelAndJoin()
            }
        }
    }
}

fun Job.joinAndLaunchAsync(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    launchAsync(name) {
        val currentJob = this@joinAndLaunchAsync
        if (!currentJob.isCompleted) {
            ensureActive()
            debug("Job($name) waiting on previous job ${jobs[currentJob]} to complete...")
            currentJob.start()
            if (currentJob is CompletableJob) { currentJob.complete() }
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
