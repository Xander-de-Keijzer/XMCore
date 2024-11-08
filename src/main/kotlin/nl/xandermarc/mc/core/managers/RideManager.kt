package nl.xandermarc.mc.core.managers

import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.debug
import nl.xandermarc.mc.lib.extensions.error
import nl.xandermarc.mc.lib.extensions.info
import nl.xandermarc.mc.lib.utils.Manager
import nl.xandermarc.mc.ride.AsyncRide
import nl.xandermarc.mc.ride.Ride
import org.bukkit.plugin.java.JavaPlugin

object RideManager : Manager {
    private val rides = mutableListOf<Ride>()
    private fun check(test: Int) {
        Globals.logger.info("TEST $test!")
    }

    internal fun register(ride: Ride): Boolean {
        if (rides.any { it.name == ride.name }) return false.debug { "Ride ${ride.name} is already registered" }
        ride.area.loadChunks(true)
        return rides.add(ride).info { "Registered ride ${ride.name}" }
    }

    internal fun unregister(rideName: String) {
        val remove = rides.filter { it.name == rideName }
        if (remove.isEmpty()) debug("Ride $rideName was not registered.")
        if (remove.size != 1) { error("Multiple rides with the name $rideName were registered ($remove)") }
        remove.forEach {
            it.area.releaseChunks()
            rides.remove(it)
            info("Removed ride $rideName($it)")
        }
    }

    fun update() {
        rides.forEach { it.update() }
    }

    suspend fun updateAsync() {
        rides.filterIsInstance<AsyncRide>().forEach { it.updateAsync() }
    }

    override fun enable(plugin: JavaPlugin) {
        //
    }

    override fun disable() {
        rides.forEach { it.disable() }
    }
}
