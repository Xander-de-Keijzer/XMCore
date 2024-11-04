package nl.xandermarc.mc.ride

import nl.xandermarc.mc.lib.area.Area
import nl.xandermarc.mc.lib.extensions.debug
import nl.xandermarc.mc.lib.extensions.info
import nl.xandermarc.mc.ride.managers.RideManager
import org.bukkit.plugin.java.JavaPlugin

abstract class SyncRide<T : Area>(
    name: String,
    area: T
) : AbstractRide<T>(name, area) {

    override fun enable(plugin: JavaPlugin) {
        require(RideManager.register(this)) { "Ride $name could not be enabled by the RideManager." }

        info("Ride $name is being enabled.")
        synchronized(state) {
            if (state == State.ENABLED) return
            check(state == State.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = State.ENABLING
        }

        debug("Initializing ride $name...")
        load()
        debug("Initializing of ride $name finished.")

        synchronized(state) {
            state = State.ENABLED
        }.info("Ride $name has been enabled.")
    }

    override fun disable() {
        RideManager.unregister(name)

        info("Ride $name is being disabled...")
        synchronized(state) {
            if (state == State.DISABLED) return
            check(state == State.ENABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = State.DISABLING
        }

        debug("Removing ride $name...")
        remove()
        debug("Removed ride $name.")

        synchronized(state) {
            state = State.DISABLED
        }.info("Ride $name has been disabled.")
    }

    override fun reset() {
        info("Ride $name is being reset...")
        synchronized(state) {
            if (state == State.ENABLED) {
                state = State.DISABLING.debug("Disabling ride $name...")
                remove()
                state = State.DISABLED.debug("Ride $name has been disabled.")
            }
            check(state == State.DISABLED) { "Ride $name is in transitioning state with no active job (stuck)." }
            state = State.ENABLING
        }

        debug("Initializing ride $name...")
        load()
        debug("Initializing of ride $name finished.")

        synchronized(state) {
            state = State.ENABLED
        }.debug("Ride $name has been enabled.")

        info("Ride $name has been reset.")
    }
}
