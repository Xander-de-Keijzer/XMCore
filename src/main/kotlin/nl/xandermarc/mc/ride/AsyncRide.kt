package nl.xandermarc.mc.ride

import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import nl.xandermarc.mc.lib.area.Area
import nl.xandermarc.mc.lib.extensions.*
import nl.xandermarc.mc.ride.managers.RideManager

abstract class AsyncRide<T : Area>(
    name: String,
    area: T
) : AbstractRide<T>(name, area) {
    private var asyncJob: Job? = null
    private val mutex = Mutex()
    override var state: State = State.DISABLED
        set(value) {
            stateChangeListeners.forEach {
                it.accept(field, value)
            }
            field = value
        }

    override fun enable() { asyncJob.joinAndLaunchReadJob("ride.enable.$name") {
        ensureActive()
        require(RideManager.register(this@AsyncRide)) { "Ride $name could not be enabled by the RideManager." }

        info("Ride $name is being enabled.")
        mutex.withLock {
            ensureActive()
            if (state == State.ENABLED) return@joinAndLaunchReadJob
            check(state == State.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = State.ENABLING
        }

        debug("Initializing ride $name asynchronously...")
        loadAsync()
        debug("Asynchronous initializing of ride $name finished.")

        ensureActive()
        launchSyncJob("ride.enable.$name.sync_load") {

            ensureActive()
            debug("Initializing ride $name synchronously...")
            load()
            debug("Synchronous initializing of ride $name finished.")

            mutex.withLock {
                ensureActive()
                state = State.ENABLED
            }.info("Ride $name has been enabled.")
        }

    }.also { asyncJob = it }.debug { "Started enable job for ride $name. ($hexKey)" } }

    override fun disable() { asyncJob.joinAndLaunchReadJob("ride.disable.$name") {
        ensureActive()
        RideManager.unregister(name)

        info("Ride $name is being disabled...")
        mutex.withLock {
            ensureActive()
            if (state == State.DISABLED) return@joinAndLaunchReadJob
            check(state == State.ENABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = State.DISABLING
        }

        debug("Removing ride $name...")
        remove()
        debug("Removed ride $name.")

        mutex.withLock {
            ensureActive()
            state = State.DISABLED
        }.info("Ride $name has been disabled.")
    }.also { asyncJob = it }.debug { "Started disable job for ride $name. ($hexKey)" } }

    override fun reset() { asyncJob.joinAndLaunchReadJob("ride.reset.$name") {
        ensureActive()
        info("Ride $name is being reset...")
        mutex.withLock {
            ensureActive()
            if (state == State.ENABLED) {
                state = State.DISABLING.debug("Disabling ride $name...")
                remove()
                state = State.DISABLED.debug("Ride $name has been disabled.")
            }
            check(state == State.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = State.ENABLING
        }

        ensureActive()
        debug("Initializing ride $name asynchronously...")
        loadAsync().
        debug("Asynchronous initializing of ride $name finished.")

        ensureActive()
        launchSyncJob("ride.reset.$name.sync_load") {

            ensureActive()
            debug("Initializing ride $name synchronously...")
            load()
            debug("Synchronous initializing of ride $name finished.")

            mutex.withLock {
                ensureActive()
                state = State.ENABLED
            }.debug("Ride $name has been enabled.")
        }.invokeOnCompletion {
            info("Ride $name has been reset.")
        }
    }.also { asyncJob = it }.debug { "Started reset job for ride $name. ($hexKey)" } }

    abstract suspend fun loadAsync()
    open suspend fun updateAsync() {}

    // Experimental
    private val stateChangeListeners = mutableListOf<StateChangeListener>(IllegalStateChangeListener())

    fun onStateChange(listener: StateChangeListener) {
        stateChangeListeners.add(listener)
    }

    fun removeStateChangeListener(listener: StateChangeListener) {
        stateChangeListeners.remove(listener)
    }

    fun interface StateChangeListener {
        fun accept(from: State, to: State)
    }

    inner class IllegalStateChangeListener : StateChangeListener {
        override fun accept(from: State, to: State) {
            when(from) {
                State.DISABLED -> check(to == State.ENABLING) { "Ride $name made an illegal state change from $from to $to." }
                State.ENABLING -> check(to == State.ENABLED) { "Ride $name made an illegal state change from $from to $to." }
                State.ENABLED -> check(to == State.DISABLING) { "Ride $name made an illegal state change from $from to $to." }
                State.DISABLING -> check(to == State.DISABLED) { "Ride $name made an illegal state change from $from to $to." }
            }.debug { "Ride $name changed state from $from to $to." }
        }
    }
}
