package nl.xandermarc.mc.lib.extensions

import kotlinx.coroutines.*
import nl.xandermarc.mc.XMCPlugin.Companion.readScope
import nl.xandermarc.mc.XMCPlugin.Companion.writeScope
import nl.xandermarc.mc.lib.logging.debug
import kotlin.coroutines.cancellation.CancellationException

enum class EnabledState {
    DISABLED,
    ENABLING,
    ENABLED,
    DISABLING
}

@OptIn(ExperimentalStdlibApi::class)
val Job.hexKey: String
    get() = hashCode().toHexString()

object JobManager {
    val jobs = hashMapOf<Job, String>()
}

fun Job?.joinAndLaunchReadJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    launchReadJob(name) {
        val currentJob = this@joinAndLaunchReadJob
        if (currentJob != null && !currentJob.isCompleted) {
            yield()
            debug("Job($name) waiting on previous job ${JobManager.jobs[currentJob]} to complete...")
            currentJob.join()
        }
        yield()
        block()
    }

fun CoroutineScope.launchJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    debug { "Job($name) is being launched on $this." }.launch(block = block).also { job ->
        val jobKey = "Job($name)#${job.hexKey}"
        JobManager.jobs[job] = jobKey
        job.invokeOnCompletion { throwable ->
            when (throwable) {
                null -> null.debug("$jobKey has been completed successfully.")
                is CancellationException -> null.debug("$jobKey has been cancelled.")
                else -> throw throwable.debug { "$jobKey has failed with exception $this" }
            }
        }
    }

fun launchReadJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    readScope.launchJob("ReadJob($name)", block)

fun launchWriteJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    writeScope.launchJob("WriteJob($name)", block)

fun test() {
    launchReadJob {
        //
    }
}
