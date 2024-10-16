package nl.xandermarc.mc.rides

import kotlinx.coroutines.*
import nl.xandermarc.mc.lib.extensions.EnabledState
import nl.xandermarc.mc.lib.extensions.hexKey
import nl.xandermarc.mc.lib.extensions.launchReadJob
import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.logging.info
import kotlin.coroutines.coroutineContext

abstract class Ride(val name: String) {
    private var asyncJob: Job? = null
    var state: EnabledState = EnabledState.DISABLED
        private set(value) {
            debug("Ride $name changed state from $field to $value")
            field = value
        }

    private suspend fun joinJob(job: Job?) {
        coroutineContext.ensureActive()
        if (job == null || job.isCompleted) return
        debug("Ride $name waiting on current asynchronous job ($job) to complete...")
        job.join()
        coroutineContext.ensureActive()
    }

    fun enable() = asyncJob.let { currentJob ->
        launchReadJob {
            joinJob(currentJob)
            synchronized(state) {
                if (state == EnabledState.ENABLED) return@launchReadJob
                check(state == EnabledState.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
                state = EnabledState.ENABLING
            }.info("Ride $name is being enabled.").debug("Initializing ride $name asynchronously...")
            ensureActive()
            initAsync().debug("Asynchronous initializing of ride $name finished.")
            ensureActive()
            launch(Dispatchers.Default) {
                debug("Initializing ride $name synchronously...")
                init().debug("Synchronous initializing of ride $name finished.")
                synchronized(state) { state = EnabledState.ENABLED }.info("Ride $name has been enabled.")
            }
        }
    }.also { asyncJob = it }.debug { "Started enable job for ride $name. ($hexKey)" }

    fun disable() = asyncJob.let { currentJob ->
        launchReadJob {
            joinJob(currentJob)
            synchronized(state) {
                if (state == EnabledState.DISABLED) return@launchReadJob
                check(state == EnabledState.ENABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
                state = EnabledState.DISABLING
            }
            info("Ride $name is being disabled...")
            remove()
            synchronized(state) { state = EnabledState.DISABLED }
            info("Ride $name has been disabled.")
        }
    }.also { asyncJob = it }.debug { "Started disable job for ride $name. ($hexKey)" }

    fun reset() = asyncJob.let { currentJob ->
        launchReadJob {
            joinJob(currentJob)
            info("Ride $name is being reset...")
            synchronized(state) {
                if (state == EnabledState.ENABLED) {
                    state = EnabledState.DISABLING.debug("Disabling ride $name...")
                    remove()
                    state = EnabledState.DISABLED.debug("Ride $name has been disabled.")
                }
                check(state == EnabledState.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            }
            ensureActive().debug("Initializing ride $name asynchronously...")
            initAsync().debug("Asynchronous initializing of ride $name finished.")
            ensureActive().debug("Initializing ride $name synchronously...")
            launch(Dispatchers.Default) {
                init().debug("Synchronous initializing of ride $name finished.")
                synchronized(state) { state = EnabledState.ENABLED }.info("Ride $name has been enabled.")
            }
            info("Ride $name has been reset.")
        }
    }.also { asyncJob = it }.debug { "Started reset job for ride $name. ($hexKey)" }

    @ExperimentalCoroutinesApi
    fun onRideTaskComplete(block: (EnabledState, EnabledState) -> Unit) {
        val currentState = synchronized(state) { state }
        asyncJob?.invokeOnCompletion {
            synchronized(state) {
                block(currentState, state)
            }
        }
    }

    protected open suspend fun initAsync() {}
    protected abstract fun init()
    protected abstract fun remove()

    open suspend fun updateAsync() {}
    abstract fun update()
}
