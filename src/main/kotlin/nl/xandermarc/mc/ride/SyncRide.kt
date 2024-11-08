package nl.xandermarc.mc.ride

import nl.xandermarc.mc.core.managers.RideManager
import nl.xandermarc.mc.lib.area.Area
import nl.xandermarc.mc.lib.extensions.debug
import nl.xandermarc.mc.lib.extensions.info
import org.bukkit.plugin.java.JavaPlugin

abstract class SyncRide(name: String, area: Area) : Ride(name, area) {
    override fun enable(plugin: JavaPlugin) {
        require(RideManager.register(this)) { "Ride $name could not be enabled by the RideManager." }
        if (state == State.ENABLED) return
        check(state == State.DISABLED) { "Ride $name is stuck in transitioning state $state." }

        info("Ride $name is being enabled.")
        state = State.ENABLING
        load()
        state = State.ENABLED
        info("Ride $name has been enabled.")
    }

    override fun disable() {
        RideManager.unregister(name)
        if (state == State.DISABLED) return
        check(state == State.ENABLED) { "Ride $name is stuck in transitioning state $state." }

        info("Ride $name is being disabled...")
        state = State.DISABLING
        remove()
        state = State.DISABLED
        info("Ride $name has been disabled.")
    }

    override fun reset() {
        check(state == State.ENABLING || state == State.DISABLING) { "Ride $name is stuck in transitioning state $state." }

        info("Ride $name is being reset...")
        if (state == State.ENABLED) {
            debug("Disabling ride $name...")
            state = State.DISABLING
            remove()
            state = State.DISABLED
        }
        debug("Ride $name is disabled and is being enabled.")
        state = State.ENABLING
        load()
        state = State.ENABLED
        info("Ride $name has been reset.")
    }
}
