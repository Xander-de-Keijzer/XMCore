package nl.xandermarc.mc.rides

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nl.xandermarc.mc.lib.logging.warn

abstract class Ride(val name: String) {
    private var asyncInitJob: Job? = null
    var enabled = false
        private set

    fun enable(): Boolean {
        if (enabled) return false
        asyncInitJob = GlobalScope.launch {
            initAsync()
            launch(Dispatchers.Default) {
                init()
            }
        }
        return true
    }
    fun disable() {
        asyncInitJob?.apply {
            if (isActive) {
                warn { "Ride $name was disabled during asynchronous initialization" }
                cancel()
            }
        }
        remove()
        enabled = false
    }
    fun reset() {
        disable()
        enable()
    }

    /**
     * Asynchronously initializes the ride.
     *
     * This method is used for resource-intensive operations needed to set up the ride, such as loading track data
     * from external files. By running asynchronously, it ensures that these heavy tasks do not block the main thread,
     * thereby maintaining optimal performance during the server startup process.
     */
    protected open fun initAsync() {}

    /**
     * Synchronously initializes the ride after asynchronous setup.
     *
     * This method is responsible for the core setup of the ride, including adding entities and defining control logic.
     * It will only be called after [initAsync] has fully completed, ensuring that all asynchronous tasks are finalized
     * before proceeding with the synchronous setup. This method runs synchronously and is scheduled by the
     * [GlobalRegionScheduler](io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler), which means it is
     * executed only after the server startup is complete. This guarantees a safe context for interacting with the server,
     * such as spawning entities and accessing the world environment.
     */
    protected abstract fun init()
    protected abstract fun remove()

    abstract fun update()
    open fun updateAsync() {}
}
