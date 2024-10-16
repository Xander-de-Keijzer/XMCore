package nl.xandermarc.mc.lib.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

val Job.hexKey: String
    get() = Integer.toHexString(key.hashCode())

fun launchReadJob(block: suspend CoroutineScope.() -> Unit): Job = readScope
    .launch(block = block)
    .also { job ->
        job.invokeOnCompletion { throwable ->
            when (throwable) {
                null -> null.debug("Job ${job.hexKey} has been completed successfully.")
                is CancellationException -> null.debug("Job ${job.hexKey} has been cancelled.")
                else -> throw throwable.debug { "Job ${job.hexKey} has failed with exception $this" }
            }
        }
    }

fun launchWriteJob(block: suspend CoroutineScope.() -> Unit): Job = writeScope
    .launch(block = block)
    .also { job ->
        job.invokeOnCompletion { throwable ->
            when (throwable) {
                null -> null.debug("Job ${job.hexKey} has been completed successfully.")
                is CancellationException -> null.debug("Job ${job.hexKey} has been cancelled.")
                else -> throw throwable.debug { "Job ${job.hexKey} has failed with exception $this" }
            }
        }
    }
