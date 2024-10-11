package nl.xandermarc.mc.rides

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.lib.logging.warn

abstract class Ride(val name: String) {
    private var asyncInitJob: Job? = null
    var enabled = false
        private set

    fun enable(): Boolean {
        if (enabled) return false
        asyncInitJob = GlobalScope.launch {
            info("Ride $name is being enabled.")
            debug("Initializing $name asynchronously...")
            initAsync()
            debug("Asynchronous initializing of ride $name finished.")
            launch(Dispatchers.Default) {
                debug("Initializing ride $name synchronously...")
                init()
                debug("Synchronous initializing of ride $name finished.")
                enabled = true
                info("Ride $name has been enabled.")
            }
        }
        return true
    }

    private fun disable() {
        info("Ride $name is being disabled...")
        remove()
        enabled = false
        info("Ride $name has been disabled.")
    }
    fun disableAsync() {
        if (asyncInitJob?.isActive == true) {
            asyncInitJob?.warn {
                "Ride $name is being disabled during asynchronous initialization, disabling the ride will be done after the initialization is complete."
            }?.invokeOnCompletion {
                disableAsync()
            }
        } else {
            disable()
        }
    }
    fun disableForce() {
        asyncInitJob?.apply {
            if (isActive) {
                warn { "Ride $name is being force disabled during asynchronous initialization, current thread (${Thread.currentThread().name}) will be blocked until the async initialization is complete. (Further execution will be cancelled)" }
                runBlocking { cancelAndJoin() }
            }
        }
        disable()
    }

    private fun reset() {
        info("Ride $name is resetting...")
        disable()
        enable()
        info("Ride $name has been reset.")
    }
    fun resetAsync() {
        if (asyncInitJob?.isActive == true) {
            asyncInitJob?.warn {
                "Ride $name is being reset during asynchronous initialization, disabling the ride will be done after the initialization is complete."
            }?.invokeOnCompletion {
                disable()
                enable()
            }
        } else {
            disable()
            enable()
        }
    }
    fun resetForce() {
        asyncInitJob?.apply {
            if (isActive) {
                warn { "Ride $name is being force reset during asynchronous initialization, current thread (${Thread.currentThread().name}) will be blocked until the async initialization is complete. (Further execution will be cancelled)" }
                runBlocking { cancelAndJoin() }
            }
        }
        reset()
    }

    /**
     * Invoke block on ride initialization complete coroutine completion
     */
    fun onRideEnableComplete(block: (Throwable?) -> Unit) {
        asyncInitJob?.invokeOnCompletion(block)
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
