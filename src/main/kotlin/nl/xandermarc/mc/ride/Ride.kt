package nl.xandermarc.mc.ride

import kotlinx.coroutines.*
import nl.xandermarc.mc.lib.extensions.debug
import nl.xandermarc.mc.lib.extensions.hexKey
import nl.xandermarc.mc.lib.extensions.info
import nl.xandermarc.mc.lib.extensions.joinAndLaunchReadJob

abstract class Ride(val name: String) {
    private var asyncJob: Job? = null
    var state: EnabledState = EnabledState.DISABLED
        private set(value) {
            debug("Ride $name changed state from $field to $value")
            field = value
        }

    fun enable() = asyncJob.joinAndLaunchReadJob("ride.enable.$name") {
        require(RideManager.register(this@Ride)) { "Ride $name could not be enabled by the RideManager." }

        synchronized(state) {
            if (state == EnabledState.ENABLED) return@joinAndLaunchReadJob
            check(state == EnabledState.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = EnabledState.ENABLING
        }.info("Ride $name is being enabled.")
        debug("Initializing ride $name asynchronously...")

        initAsync()
        debug("Asynchronous initializing of ride $name finished.")
        yield()

        launch(Dispatchers.Default) {

            debug("Initializing ride $name synchronously...")
            init()
            debug("Synchronous initializing of ride $name finished.")
            yield()

            synchronized(state) { state = EnabledState.ENABLED }.info("Ride $name has been enabled.")
        }

    }.also { asyncJob = it }.debug { "Started enable job for ride $name. ($hexKey)" }

    fun disable() = asyncJob.joinAndLaunchReadJob("ride.disable.$name") {
        RideManager.unregister(name)

        synchronized(state) {
            if (state == EnabledState.DISABLED) return@joinAndLaunchReadJob
            check(state == EnabledState.ENABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = EnabledState.DISABLING
        }.info("Ride $name is being disabled...")

        remove()
        synchronized(state) { state = EnabledState.DISABLED }.info("Ride $name has been disabled.")
    }.also { asyncJob = it }.debug { "Started disable job for ride $name. ($hexKey)" }

    fun reset() = asyncJob.joinAndLaunchReadJob("ride.reset.$name") {
        info("Ride $name is being reset...")

        synchronized(state) {
            if (state == EnabledState.ENABLED) {
                state = EnabledState.DISABLING.debug("Disabling ride $name...")
                remove()
                state = EnabledState.DISABLED.debug("Ride $name has been disabled.")
            }
            check(state == EnabledState.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
        }

        debug("Initializing ride $name asynchronously...")
        initAsync().
        debug("Asynchronous initializing of ride $name finished.")
        yield()

        launch(Dispatchers.Default) {

            debug("Initializing ride $name synchronously...")
            init()
            debug("Synchronous initializing of ride $name finished.")
            yield()

            synchronized(state) { state = EnabledState.ENABLED }.debug("Ride $name has been enabled.")
        }.invokeOnCompletion {
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

    enum class EnabledState {
        DISABLED,
        ENABLING,
        ENABLED,
        DISABLING
    }

    protected open suspend fun initAsync() {}
    protected abstract fun init()
    protected abstract fun remove()

    open suspend fun updateAsync() {}
    abstract fun update()
}
