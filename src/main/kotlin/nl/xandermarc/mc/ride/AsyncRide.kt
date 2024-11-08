package nl.xandermarc.mc.ride

import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import nl.xandermarc.mc.core.managers.RideManager
import nl.xandermarc.mc.lib.area.Area
import nl.xandermarc.mc.lib.extensions.*
import org.bukkit.plugin.java.JavaPlugin

abstract class AsyncRide(name: String, area: Area): Ride(name, area) {
    private var asyncJob: Job = Job()

    override fun enable(plugin: JavaPlugin) { asyncJob.joinAndLaunchAsync("ride.enable.$name") {
        if (state == State.ENABLED) return@joinAndLaunchAsync
        check(state == State.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
        require(RideManager.register(this@AsyncRide)) { "Ride $name could not be enabled by the RideManager." }

        ensureActive().info("Ride $name is being enabled...")
        state = State.ENABLING
        loadAsync()
        ensureActive().debug("Ride $name is being loaded synchronously...")
        launchSync("ride.enable.$name.sync_load") {
            ensureActive()
            load()
            state = State.ENABLED
            info("Ride $name has been enabled.")
        }
    }.also { asyncJob = it }.debug { "Started enable job for ride $name. ($hexKey)" } }

    override fun disable() { asyncJob.joinAndLaunchAsync("ride.disable.$name") {
        if (state == State.DISABLED) return@joinAndLaunchAsync
        check(state == State.ENABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
        RideManager.unregister(name)

        ensureActive().info("Ride $name is being disabled...")
        state = State.DISABLING
        remove()
        state = State.DISABLED
        info("Ride $name has been disabled.")
    }.also { asyncJob = it }.debug { "Started disable job for ride $name. ($hexKey)" } }

    override fun reset() { asyncJob.joinAndLaunchAsync("ride.reset.$name") {
        ensureActive().info("Ride $name is being reset...")
        if (state == State.ENABLED) {
            debug("Disabling ride $name...")
            state = State.DISABLING
            remove()
            state = State.DISABLED
        }

        debug("Ride $name is disabled and is being enabled.")
        state = State.ENABLING
        loadAsync()
        ensureActive().debug("Ride $name is being loaded synchronously...")
        launchSync("ride.reset.$name.sync_load") {
            ensureActive()
            load()
            state = State.ENABLED
            info("Ride $name has been reset.")
        }
    }.also { asyncJob = it }.debug { "Started reset job for ride $name. ($hexKey)" } }

    abstract suspend fun loadAsync()
    open suspend fun updateAsync() {}
}
