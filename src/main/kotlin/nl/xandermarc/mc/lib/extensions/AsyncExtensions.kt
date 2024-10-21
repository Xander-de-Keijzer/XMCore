package nl.xandermarc.mc.lib.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import nl.xandermarc.mc.core.XMC
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalStdlibApi::class)
val Job.hexKey: String get() = hashCode().toHexString()

fun Job?.joinAndLaunchReadJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    XMC.launchReadJob(name) {
        val currentJob = this@joinAndLaunchReadJob
        if (currentJob != null && !currentJob.isCompleted) {
            yield()
            debug("Job($name) waiting on previous job ${XMC.jobs[currentJob]} to complete...")
            currentJob.join()
        }
        yield()
        block()
    }

fun CoroutineScope.launchJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
    debug { "Job($name) is being launched on $this." }.launch(block = block).also { job ->
        val jobKey = "Job($name)#${job.hexKey}"
        XMC.jobs[job] = jobKey
        job.invokeOnCompletion { throwable ->
            when (throwable) {
                null -> null.debug("$jobKey has been completed successfully.")
                is CancellationException -> null.debug("$jobKey has been cancelled.")
                else -> throw throwable.debug { "$jobKey has failed with exception $this" }
            }
        }
    }


